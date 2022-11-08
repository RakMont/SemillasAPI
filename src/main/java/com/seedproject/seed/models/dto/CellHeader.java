package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellHeader {
    public String key;
    public Integer order;
    public String propertyType;
    public boolean canSort;
    public String toolTip;

    public CellHeader(String key, Integer order, String propertyType, boolean canSort, String toolTip) {
        this.key = key;
        this.order = order;
        this.propertyType = propertyType;
        this.canSort = canSort;
        this.toolTip = toolTip;
    }
}
