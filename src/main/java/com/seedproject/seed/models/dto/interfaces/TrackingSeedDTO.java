package com.seedproject.seed.models.dto.interfaces;

import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.models.enums.Status;
import lombok.*;

import javax.persistence.Entity;
import java.util.Date;

//@Getter
//@Setter
public interface TrackingSeedDTO {
     Long getTracking_assignment_id();
     Long getVolunter_id();
     Long getContributor_id();
     Date getStart_date();
     Date getEnd_date();
     Status getStatus();
     String getName();
     String getLastname();
     String getPhone();
     String getDni();
     String getEmail();
     Long getContribution_config_id();
     ContributionType getContribution_key();
     int getContributor_state();
     String getTotal_contribution();
     Date getLast_payment_date();
}
