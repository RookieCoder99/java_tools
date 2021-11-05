package com.xjtu.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class BaikeRelation {
    private long relationId;
    private String relationName;

    public BaikeRelation(String relationName) {
        this.relationName = relationName;
    }
}
