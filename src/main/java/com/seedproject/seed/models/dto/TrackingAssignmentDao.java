package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TrackingAssignmentDao {
    private String tracking_assignment_id;

    private String volunter_id;

    private String contributor_id;

    private Date start_date;

    private Date end_date;

    private Status status;

    public TrackingAssignmentDao() {
    }
}
