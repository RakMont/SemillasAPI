package com.seedproject.seed.models.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "unique_contribution")
@Getter
@Setter
public class UniqueContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unique_contribution_id")
    private Long unique_contribution_id;

    @Column(name = "date_contribution")
    private Date date_contribution;

    @NotNull(message = "The contribution must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contribution_id", referencedColumnName = "contribution_id")
    private Contribution contribution;

}