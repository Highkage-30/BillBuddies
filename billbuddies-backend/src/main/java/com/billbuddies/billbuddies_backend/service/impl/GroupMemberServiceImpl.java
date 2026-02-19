package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.AddGroupMembersRequestDto;
import com.billbuddies.billbuddies_backend.dto.UploadMemberRowDto;
import com.billbuddies.billbuddies_backend.dto.GroupMemberResponseDto;
import com.billbuddies.billbuddies_backend.entity.GroupInfo;
import com.billbuddies.billbuddies_backend.entity.GroupMember;
import com.billbuddies.billbuddies_backend.entity.GroupMemberId;
import com.billbuddies.billbuddies_backend.entity.Member;
import com.billbuddies.billbuddies_backend.exception.BadRequestException;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupMemberRepository;
import com.billbuddies.billbuddies_backend.repository.MemberRepository;
import com.billbuddies.billbuddies_backend.service.GroupMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {
    private static final String RESERVED_NAME = "BillBuddy";

    private final GroupMemberRepository groupMemberRepository;
    private final GroupInfoRepository groupInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public List<GroupMemberResponseDto> getGroupMembers(Long groupId) {

        log.info("Fetching members for groupId={}", groupId);

        // Validate group exists
        if (!groupInfoRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        return groupMemberRepository.findByGroup_GroupId(groupId)
                .stream()
                .map(this::toDto)
                .toList();
    }
    @Override
    @Transactional
    public void addMembers(Long groupId, AddGroupMembersRequestDto request) {

        if (request.getMemberNames() == null || request.getMemberNames().isEmpty()) {
            throw new BadRequestException("Member list cannot be empty");
        }

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found with id: " + groupId));

        // Remove duplicates from payload (case-insensitive)
        Set<String> normalizedNames = new HashSet<>();
        for (String name : request.getMemberNames()) {
            if (name == null || name.isBlank()) {
                throw new BadRequestException("Member name cannot be blank");
            }
            normalizedNames.add(name.trim().toLowerCase());
        }

        for (String normalizedName : normalizedNames) {
            // 🔥 Skip BillBuddy silently
            if (isReserved(normalizedName)) {
                log.warn("Skipping reserved system name: {}", normalizedName);
                continue;
            }
            // 1️⃣ Find or create member
            Member member = memberRepository
                    .findByMemberNameIgnoreCase(normalizedName)
                    .orElseGet(() -> {
                        log.info("Creating new member: {}", normalizedName);
                        return memberRepository.save(
                                Member.builder()
                                        .memberName(capitalize(normalizedName))
                                        .build()
                        );
                    });

            // 2️⃣ Add to group if not already present
            GroupMemberId id =
                    new GroupMemberId(groupId, member.getMemberId());

            if (groupMemberRepository.existsById(id)) {
                log.info("Member {} already in group {}, skipping",
                        member.getMemberName(), groupId);
                continue;
            }

            groupMemberRepository.save(
                    GroupMember.builder()
                            .id(id)
                            .group(group)
                            .member(member)
                            .joinedAt(LocalDateTime.now())
                            .build()
            );
        }

        log.info("Members added successfully to groupId={}", groupId);
    }


    @Override
    @Transactional
    public void uploadGroupMembers(Long groupId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BadRequestException("Invalid file name");
        }

        // Validate group exists
        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Group not found: " + groupId));

        List<UploadMemberRowDto> rows =
                filename.toLowerCase().endsWith(".csv")
                        ? parseCsv(file)
                        : parseExcel(file);

        if (rows.isEmpty()) {
            throw new BadRequestException("No member rows found in file");
        }

        // 🔥 Convert to existing addMembers API
        List<String> memberNames = rows.stream()
                .map(UploadMemberRowDto::getMemberName)
                .toList();

        AddGroupMembersRequestDto request = new AddGroupMembersRequestDto();
        request.setMemberNames(memberNames);

        addMembers(groupId, request);

        log.info("Member upload successful for groupId={}", groupId);
    }

    // ================= CSV =================

    private List<UploadMemberRowDto> parseCsv(MultipartFile file) {

        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .parse(reader)
        ) {

            validateHeaders(parser.getHeaderMap().keySet());

            List<UploadMemberRowDto> rows = new ArrayList<>();
            int rowNumber = 2;

            for (CSVRecord record : parser) {

                try {

                    String memberName = record.get("Member Name");

                    if (memberName == null || memberName.isBlank()) {
                        throw new BadRequestException("Member Name cannot be empty");
                    }

                    rows.add(new UploadMemberRowDto() {{
                        setMemberName(memberName.trim());
                    }});

                } catch (Exception e) {
                    throw new BadRequestException(
                            "Invalid data at row " + rowNumber + ": " + e.getMessage()
                    );
                }

                rowNumber++;
            }

            return rows;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid CSV format");
        }
    }

    // ================= EXCEL =================

    private List<UploadMemberRowDto> parseExcel(MultipartFile file) {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new BadRequestException("Excel file has no header row");
            }

            Map<String, Integer> headerIndex = new HashMap<>();

            for (Cell cell : headerRow) {
                headerIndex.put(
                        cell.getStringCellValue().trim().toLowerCase(),
                        cell.getColumnIndex()
                );
            }

            validateHeaders(headerIndex.keySet());

            Integer nameIndex = headerIndex.get("member name");

            List<UploadMemberRowDto> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                int excelRow = i + 1;

                try {

                    String name = row.getCell(nameIndex)
                            .getStringCellValue()
                            .trim();

                    if (name.isBlank()) {
                        throw new BadRequestException("Member Name cannot be empty");
                    }

                    rows.add(new UploadMemberRowDto() {{
                        setMemberName(name);
                    }});

                } catch (Exception e) {
                    throw new BadRequestException(
                            "Invalid data at row " + excelRow + ": " + e.getMessage()
                    );
                }
            }

            return rows;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Invalid Excel format");
        }
    }

    private void validateHeaders(Set<String> headers) {

        boolean valid = headers.stream()
                .anyMatch(h -> h.equalsIgnoreCase("member name"));

        if (!valid) {
            throw new BadRequestException(
                    "Missing required column: Member Name"
            );
        }
    }
    private boolean isReserved(String memberName) {
        return memberName != null &&
                memberName.equalsIgnoreCase(RESERVED_NAME);
    }


    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private GroupMemberResponseDto toDto(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .memberId(groupMember.getMember().getMemberId())
                .memberName(groupMember.getMember().getMemberName())
                .build();
    }
}
