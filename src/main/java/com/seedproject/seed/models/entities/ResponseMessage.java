package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMessage {
    private String status;
    private String message;
    private Integer statusCode;

    public ResponseMessage(String status, String message, Integer statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }
}
