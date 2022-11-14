package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.enums.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResponseMessage {
    private String message;
    private ResponseStatus status;

    public RequestResponseMessage(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public RequestResponseMessage() {
    }
}


