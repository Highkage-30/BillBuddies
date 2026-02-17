package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "member",
        uniqueConstraints = @UniqueConstraint(columnNames = "member_name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "member_seq_gen"
    )
    @SequenceGenerator(
            name = "member_seq_gen",
            sequenceName = "member_seq",
            allocationSize = 1
    )
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_name", nullable = false, unique = true)
    private String memberName;
}