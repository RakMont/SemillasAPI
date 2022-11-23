package com.seedproject.seed.models.entities;

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
}
