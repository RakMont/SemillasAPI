package com.seedproject.seed.models.dto;


import com.seedproject.seed.models.enums.ContributionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProcessSeedDTO {

    private Long processed_contributor_id;

    private Long contributor_id;

    /*@JsonFormat(pattern="dd-MM-yyyy")
    private Date processed_date;*/

    private String process_reason;

    private Long processVolunterId;

    private int state;

    private ContributionType contributionType;

    //@JsonFormat(pattern="dd-MM-yyyy")
    private Date contributionStartDate;

    //@JsonFormat(pattern="dd-MM-yyyy")
    private Date contributionEndDate;

    public ProcessSeedDTO(Long processed_contributor_id, Long contributor_id, Date processed_date, String process_reason, Long processVolunterId, int state) {
        this.processed_contributor_id = processed_contributor_id;
        this.contributor_id = contributor_id;
        //this.processed_date = processed_date;
        this.process_reason = process_reason;
        this.processVolunterId = processVolunterId;
        this.state = state;
    }
}
