package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.Volunter;
import lombok.Value;

@Value
public class ComboVolunteer {
    String volunter_id;
    String name;
    String lastname;
    String largename;
    String email;
    String phone;
    String dni;

    public ComboVolunteer(String volunter_id, Volunter volunter) {
        this.volunter_id = volunter_id;
        this.name = volunter.getUser().getName();
        this.lastname = volunter.getUser().getLastname();
        this.largename = volunter.getFullName();
        this.email = volunter.getUser().getEmail();
        this.phone = volunter.getUser().getPhone();
        this.dni = volunter.getUser().getDni();
    }
}
