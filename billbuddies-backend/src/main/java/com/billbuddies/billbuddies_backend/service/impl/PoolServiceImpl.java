package com.billbuddies.billbuddies_backend.service.impl;

import com.billbuddies.billbuddies_backend.dto.GroupPoolResponseDto;
import com.billbuddies.billbuddies_backend.entity.GroupInfo;
import com.billbuddies.billbuddies_backend.entity.GroupPool;
import com.billbuddies.billbuddies_backend.exception.ResourceNotFoundException;
import com.billbuddies.billbuddies_backend.repository.GroupInfoRepository;
import com.billbuddies.billbuddies_backend.repository.GroupPoolRepository;
import com.billbuddies.billbuddies_backend.service.PoolService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class PoolServiceImpl implements PoolService {

    private final GroupInfoRepository groupInfoRepository;
    private final GroupPoolRepository groupPoolRepository;

    @Override
    @Transactional
    public GroupPoolResponseDto getGroupPool(Long groupId) {

        log.info("Fetching pool for groupId={}", groupId);

        GroupInfo group = groupInfoRepository.findById(groupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Group not found with id: " + groupId
                        ));

        GroupPool pool = groupPoolRepository
                .findByGroup_GroupId(groupId)
                .orElseGet(() -> {
                    log.info("Pool not found. Auto-creating pool for groupId={}", groupId);
                    return groupPoolRepository.save(
                            GroupPool.builder()
                                    .group(group)
                                    .balance(BigDecimal.ZERO)
                                    .build()
                    );
                });

        return GroupPoolResponseDto.builder()
                .poolId(pool.getPoolId())
                .groupId(group.getGroupId())
                .balance(pool.getBalance())
                .createdAt(pool.getCreatedAt())
                .build();
    }

}
