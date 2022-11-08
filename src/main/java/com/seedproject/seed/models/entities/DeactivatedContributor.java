package com.seedproject.seed.models.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "deactivated_contributor")
@Getter
@Setter
public class DeactivatedContributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deactivated_contributor_id")
    private Long deactivated_contributor_id;

    @Column(name = "deactivation_reason")
    private String deactivationReason;

    @Column(name = "deactivation_date")
    private Date deactivationDate;

    @Column(name = "reactivation_date")
    private Date reactivationDate;

    @NotNull(message = "The contributor must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contributor_id", referencedColumnName = "contributor_id")
    private Contributor contributor;

    @NotNull(message = "The volunter must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reg_volunter_id", referencedColumnName = "volunter_id")
    private Volunter regVolunter;
}