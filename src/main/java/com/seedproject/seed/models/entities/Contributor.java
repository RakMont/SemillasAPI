package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contributor")
@Getter
@Setter
public class Contributor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contributor_id")
    private Long contributor_id;

    @Column(name = "address")
    private String address;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "is_foreign")
    private Boolean isForeign;

    @Column(name = "send_date")
    private Date send_date;

    @NotNull(message = "The register volunter must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_volunter_id", referencedColumnName = "volunter_id")
    private Volunter registerVolunter;

    //@Enumerated(EnumType.STRING)
    @Column(name = "contributor_state")
    private int contributorState;

    @Column(name = "register_date")
    private Date register_date;

    @Column(name = "register_exist")
    private Boolean register_exist;

    @NotNull(message = "The person must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    /*@NotNull(message = "The Config must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contribution_config_id", referencedColumnName = "contribution_config_id")
    private ContributionConfig contributionConfig;*/

    @NotNull(message = "The Config must not be null")
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "seed_configuration",
            joinColumns = @JoinColumn(name = "contributor_id", referencedColumnName = "contributor_id"),
            inverseJoinColumns = @JoinColumn(name = "contribution_config_id"))
    private List<ContributionConfig> seedConfigurations = new ArrayList<>();

    @OneToOne(mappedBy = "contributor")
    private DeactivatedContributor deactivatedContributor;

    @OneToOne(mappedBy = "contributor")
    private ProcessedContributor processedContributor;

    public ContributionConfig getActiveContribution(){
        ContributionConfig contributionConfig = null;
        for(ContributionConfig cr: this.seedConfigurations){
            if(cr.getIs_active()) contributionConfig = cr;
        }
        return contributionConfig;
    }
    public String getSeedFullName(){
        return this.getUser().getName() + " " + getUser().getLastname();
    }
}