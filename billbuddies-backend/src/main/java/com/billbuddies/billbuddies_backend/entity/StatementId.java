package com.billbuddies.billbuddies_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementId implements Serializable {

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "member_id")
    private Long memberId;
}
