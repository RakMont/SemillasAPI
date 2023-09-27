package com.seedproject.seed.models.dto.interfaces;

import com.seedproject.seed.models.enums.ContributionType;

public interface SeedDTO {
    Long getContributor_id();
    String getSeed_name();
    String getPhone();
    String getDni();
    String getEmail();
    Long getContribution_config_id();
    ContributionType getContribution_key();
    int getContributor_state();
    Long getTracking_assignment_id();
    Long getTr_volunteer();
    String getVol_name();
}
