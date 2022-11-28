package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "activity")
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length=1000)
    private String description;

    @Column(name = "description_translate", length=1000)
    private String descriptionTranslate;

    @NotNull(message = "The volunteer must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reg_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter regVolunteer;
}
