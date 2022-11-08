package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellProperty {
    public String color;
    public boolean clickable;
    public String clickedAction;
    public String cellTooltip;

    public CellProperty(String color, boolean clickable, String clickedAction, String cellTooltip) {
        this.color = color;
        this.clickable = clickable;
        this.clickedAction = clickedAction;
        this.cellTooltip = cellTooltip;
    }
}