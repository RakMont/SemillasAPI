package com.seedproject.seed.models.entities;

import com.seedproject.seed.models.enums.ContributionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contribution_config")
@Getter
@Setter
public class ContributionConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_config_id")
    private Long contribution_config_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribution_key")
    private ContributionType contribution_key;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unique_contribution_id", referencedColumnName = "unique_contribution_id")
    private UniqueContribution uniqueContribution;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "const_contribution_id", referencedColumnName = "const_contribution_id")
    private ConstantContribution constantContribution;

    @Column(name = "register_date")
    private Date register_date;

    @Column(name = "is_active")
    private Boolean is_active;

}