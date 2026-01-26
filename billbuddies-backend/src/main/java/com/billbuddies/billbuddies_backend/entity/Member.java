package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "member",
        indexes = @Index(columnList = "member_name", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen")
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", nullable = false, unique = true)
    private String memberName;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<GroupMember> groupMembers = new HashSet<>();
}
