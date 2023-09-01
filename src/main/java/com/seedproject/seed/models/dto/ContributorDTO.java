package com.seedproject.seed.models.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.enums.ContributionType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ContributorDTO implements Serializable {
    private Long seedId;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String dni;
    //@JsonFormat(pattern="yyyy-MM-dd")
    private Date birthdate;
    private String address;
    private String country;
    private String city;
    //@JsonFormat(pattern="yyyy-MM-dd")
    private Date send_date;
    private int contributorState;

    private String registerVolunteer;
    private ContributionConfigDTO contributionConfig;
    private String contributionType;
    private Long contribution_id;
    //@JsonFormat(pattern="yyyy-MM-dd")
    private Date acceptedDate;

    public ContributorDTO(Contributor applicant) {
        seedId=applicant.getContributor_id();
        name=applicant.getUser().getName();
        lastname=applicant.getUser().getLastname();
        email=applicant.getUser().getEmail();
        phone=applicant.getUser().getPhone();
        dni=applicant.getUser().getDni();
        birthdate=applicant.getUser().getBirthdate();
        address=applicant.getAddress();
        country=applicant.getCountry();
        city=applicant.getCity();
        send_date=applicant.getSend_date();
        contributorState =applicant.getContributorState();
        contributionType= applicant.getContributionConfig().getContribution_key().equals(ContributionType.APORTE_CONSTANTE) ? "Aporte Constante" : "Aporte Único";
        //contribution_id=applicant.getContributor().getContributionConfig().getContribution_id();
        //acepted_date=applicant.getAcepted_date();
    }
    public void saveContributionConfig(ContributionConfigDTO contributionConfigDTO){
        contributionConfig=contributionConfigDTO;
    }

}
