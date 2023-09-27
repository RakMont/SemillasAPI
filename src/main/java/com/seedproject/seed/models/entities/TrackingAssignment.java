package com.seedproject.seed.models.entities;

import com.seedproject.seed.models.dao.TrackingAssignmentDao;
import com.seedproject.seed.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tracking_assignment")
@Getter
@Setter
public class TrackingAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracking_assignment_id")
    private Long tracking_assignment_id;

    @Column(name = "volunter_id")
    private Long volunter_id;

    @Column(name = "contributor_id")
    private Long contributor_id;

    @Column(name = "start_date")
    private Date start_date;

    @Column(name = "end_date")
    private Date end_date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public TrackingAssignment() {
    }
    public TrackingAssignment(TrackingAssignmentDao trackingAssignmentDao) {
        this.tracking_assignment_id = trackingAssignmentDao.getTracking_assignment_id() != null ?
                Long.parseLong(trackingAssignmentDao.getTracking_assignment_id()) : null;
        this.volunter_id = Long.parseLong(trackingAssignmentDao.getVolunter_id());
        this.contributor_id = Long.parseLong(trackingAssignmentDao.getContributor_id());
        this.start_date = trackingAssignmentDao.getStart_date();
        this.end_date = trackingAssignmentDao.getEnd_date();
        this.status=trackingAssignmentDao.getStatus();
    }
}
