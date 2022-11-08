package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seedproject.seed.models.enums.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private RoleName role_name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<Volunter> volunters;
}