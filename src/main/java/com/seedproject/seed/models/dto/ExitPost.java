package com.seedproject.seed.models.dto;


import com.seedproject.seed.models.entities.ExitMessage;
import lombok.Getter;

import java.util.Date;

@Getter
public class ExitPost {
    Long message_id;
    String message;
    Long volunteerId;
    Date registerDate;

    public ExitPost(ExitMessage exitMessage) {
        this.message_id = exitMessage.getMessage_id();
        this.message = exitMessage.getMessage();
        this.volunteerId = exitMessage.getVolunter().getVolunterId();
        this.registerDate = exitMessage.getRegisterDate();
    }
}

