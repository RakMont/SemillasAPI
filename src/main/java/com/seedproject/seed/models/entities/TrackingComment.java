package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tracking_comment")
@Getter
@Setter
public class TrackingComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tracking_comment_id;

    @NotNull(message = "The souvenir tracking must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_volunter_id", referencedColumnName = "volunter_id")
    private Volunter registerVolunter;

}