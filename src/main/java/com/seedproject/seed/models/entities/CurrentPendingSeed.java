package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "current_pending_seed")
@Getter
@Setter
public class CurrentPendingSeed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "current_pending_seed_id")
    private Long CurrentPendingSeedId;

    @NotNull(message = "The volunteer must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "volunteer_id", referencedColumnName = "volunter_id")
    private Volunter volunteer;

    @Column(name = "sent_email")
    private boolean sentEmail;

    @Column(name = "done_donation")
    private boolean doneDonation;
}
