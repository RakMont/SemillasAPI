package com.seedproject.seed.models.dto;

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

    public ComboVolunteer(String volunter_id, String name, String lastname, String largename, String email, String phone, String dni) {
        this.volunter_id = volunter_id;
        this.name = name;
        this.lastname = lastname;
        this.largename = largename;
        this.email = email;
        this.phone = phone;
        this.dni = dni;
    }
}
