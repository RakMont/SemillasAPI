package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MenuItem {
    private String text;
    private String icon;
    private String routerLink;


    public MenuItem(String text, String icon, String routerLink) {
        this.text = text;
        this.icon = icon;
        this.routerLink = routerLink;
    }
}
