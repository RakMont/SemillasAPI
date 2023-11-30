package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "seed_user")
@Getter
@Setter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "dni",unique = true)
    private String dni;

    @Column(name = "birthdate")
    private Date birthdate;

    @OneToOne(mappedBy = "user")
    private Volunter volunter;

    public String getFullName() {
        return name + " " + lastname;
    }
}