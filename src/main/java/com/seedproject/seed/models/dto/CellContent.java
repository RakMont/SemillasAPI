package com.seedproject.seed.models.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CellContent {
    public String contentType;
    public String iconName;
    public String color;
    public boolean clickable;
    public String clickedAction;
    public String contentTooltip;
    public String content;
    public List<CellParam> params;

    public CellContent(String contentType, String iconName, String color, boolean clickable, String clickedAction, String contentTooltip, String content, List<CellParam> params) {
        this.contentType = contentType;
        this.iconName = iconName;
        this.color = color;
        this.clickable = clickable;
        this.clickedAction = clickedAction;
        this.contentTooltip = contentTooltip;
        this.content = content;
        this.params = params;
    }
}
