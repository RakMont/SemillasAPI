package com.seedproject.seed.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtRequest {
    private String username;
    private String password;


    public JwtRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}