package com.billbuddies.billbuddies_backend.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StatementId implements Serializable {
    private Long groupId;
    private String memberName;
}
