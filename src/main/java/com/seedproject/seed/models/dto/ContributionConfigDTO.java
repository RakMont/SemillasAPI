package com.seedproject.seed.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seedproject.seed.models.entities.ContributionConfig;
import com.seedproject.seed.models.enums.ContributionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionConfigDTO {
    private Long id;
    private ContributionType contributionType;
    //@JsonFormat(pattern="yyyy-MM-dd")
    private Date registerDate;

    private ContributionDTO contribution;
    private Boolean isActive;
    public ContributionConfigDTO(ContributionConfig contributionConfig) {
        this.id = contributionConfig.getContribution_config_id();
        this.contributionType = contributionConfig.getContribution_key();
        this.registerDate = contributionConfig.getRegister_date();
        this.isActive = contributionConfig.getIs_active();
    }
}
