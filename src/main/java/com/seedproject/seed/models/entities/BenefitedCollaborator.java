package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/*
@Entity
@Table(name = "benefited_seed")
@Getter
@Setter
public class BenefitedCollaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benefited_collaborator_id")
    private Long benefited_collaborator_id;

    @Column(name = "selected_date")
    private Date selectedDate;

    @Column(name = "observation")
    private String observation;

    @Column(name = "city")
    private String city;

    @NotNull(message = "The contributor must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contributor_id", referencedColumnName = "contributor_id")
    private Contributor contributor;

    @NotNull(message = "The volunter  must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter registerVolunteer;
}

*/