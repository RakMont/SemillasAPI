package com.seedproject.seed.models.dto;

import lombok.Value;

@Value
public class ComboSeed {
    Long contributor_id;
    String name;
    String lastname;
    String largename;
    String email;
    String phone;
    String dni;

    public ComboSeed(Long contributor_id, String name, String lastname, String largename, String email, String phone, String dni) {
        this.contributor_id = contributor_id;
        this.name = name;
        this.lastname = lastname;
        this.largename = largename;
        this.email = email;
        this.phone = phone;
        this.dni = dni;
    }
}
