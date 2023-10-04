package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "seed_souvenir_tracking")
@Getter
@Setter
public class SeedSouvenirTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seed_souvenir_tracking_id")
    private Long seedSouvenirTrackingId;

    @Column(name = "selected_date")
    private Date selectedDate;

    @Column(name = "souvenir_send_date")
    private Date souvenirSendDate;

    @Column(name = "tracking_status")
    private TrackingStatus trackingStatus;

    @Column(name = "delivery_amount")
    private int spentAmount;

    @Column(name = "chosen_city")
    private String chosenCity;

    @Column(name = "observation")
    private String observation;

    @Column(name = "register_exist")
    private Boolean register_exist;

    @NotNull(message = "The contributor must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "benefited_contributor_id", referencedColumnName = "contributor_id")
    private Contributor benefitedContributor;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "benefited_collaborator_id", referencedColumnName = "benefited_collaborator_id")
    private BenefitedCollaborator benefitedCollaborator;
*/
    @NotNull(message = "The volunter  must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_in_charge_id", referencedColumnName = "volunter_id")
    private Volunter volunteerInCharge;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "souvenir_tracking_comment",
            joinColumns = @JoinColumn(name = "souvenir_tracking_id", referencedColumnName = "seed_souvenir_tracking_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_record_id"))
    private List<CommentRecord> souvenirTrackingComments = new ArrayList<>();
}
