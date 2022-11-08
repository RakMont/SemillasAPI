package com.seedproject.seed.models.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class VolunterDTO {

    private Long volunterId;

    private Long userId;

    private String name;

    private String lastname;

    private String email;

    private String phone;

    private String dni;

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthdate;

    private Date entry_date;

    private Date exit_date;

    private Status status;

    private List<Role> roles;

    public VolunterDTO(Volunter volunter) {

        volunterId =volunter.getVolunterId();
        entry_date=volunter.getEntryDate();
        exit_date=volunter.getExitDate();
        status=volunter.getStatus();
        name=volunter.getUser().getName();
        lastname=volunter.getUser().getLastname();
        email=volunter.getUser().getEmail();
        phone=volunter.getUser().getPhone();
        dni=volunter.getUser().getDni();
        birthdate=volunter.getUser().getBirthdate();
        roles=volunter.getRoles();
        userId =volunter.getUser().getUserId();
    }
}
