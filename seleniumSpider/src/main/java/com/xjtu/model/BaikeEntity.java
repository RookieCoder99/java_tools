package com.xjtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BaikeEntity {
    private long entityId;
    private String entityName;

    private String abstractText;
    private Map<String,BaikeRelation> relationMap = new HashMap<>();



    public BaikeEntity(String entityName) {
        this.entityName = entityName;
    }

    public BaikeEntity(long entityId, String entityName) {
        this(entityName);
        this.entityId = entityId;
    }

    public BaikeEntity(long entityId, String entityName, String abstractText) {
        this(entityId,entityName);
        this.abstractText = abstractText;
    }
}
