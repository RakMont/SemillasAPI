package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DesMenu {
    private String text;
    private String icon;

    private List<MenuItem> children;


    public DesMenu(String text, String icon) {
        this.text = text;
        this.icon = icon;
    }
}
