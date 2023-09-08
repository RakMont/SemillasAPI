package com.seedproject.seed.models.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.seedproject.seed.models.entities.ExitMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExitPost {
    Long message_id;
    String message;
    String volunteerId;
    @JsonFormat(pattern="yyyy-MM-dd")
    Date registerDate;

    public ExitPost() {
    }

    public ExitPost(ExitMessage exitMessage) {
        this.message_id = exitMessage.getMessage_id();
        this.message = exitMessage.getMessage();
        this.volunteerId = exitMessage.getVolunter().getVolunterId().toString();
        this.registerDate = exitMessage.getRegisterDate();
    }
}

