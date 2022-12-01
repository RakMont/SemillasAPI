package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "souvenir_tracking")
@Getter
@Setter
public class SouvenirTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "souvenir_tracking_id")
    private Long souvenir_tracking_id;

    @Column(name = "souvenir_send_date")
    private Date souvenir_send_date;

    @Column(name = "tracking_status")
    private TrackingStatus trackingStatus;

    @Column(name = "spent_amount")
    private int spentAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "benefited_collaborator_id", referencedColumnName = "benefited_collaborator_id")
    private BenefitedCollaborator benefitedCollaborator;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "souvenir_tracking_comment",
            joinColumns = @JoinColumn(name = "souvenir_tracking_id", referencedColumnName = "souvenir_tracking_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_record_id"))
    private List<CommentRecord> souvenirTrackingComments = new ArrayList<>();
}
