package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.DeactivatedContributor;
import com.seedproject.seed.models.enums.ContributorState;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DeactivateContributorDTO {
    private String id;
    private Date deactivationDate;
    private String deactivationReason;
    private Date reactivationDate;
    private ContributorState contributorState;
    private String contributorId;

    public DeactivateContributorDTO(DeactivatedContributor deactivatedContributor) {
        this.deactivationDate = deactivatedContributor.getDeactivationDate();
        this.deactivationReason = deactivatedContributor.getDeactivationReason();
        this.reactivationDate = deactivatedContributor.getReactivationDate();
    }
    public  DeactivateContributorDTO(){}
}
