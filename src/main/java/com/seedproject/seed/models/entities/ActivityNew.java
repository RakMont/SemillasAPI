package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity_new")
@Getter
@Setter
public class ActivityNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length=1000)
    private String description;

    @Column(name = "len")
    private String len;

    @NotNull(message = "The volunteer must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reg_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter regVolunteer;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "activity_new_translates",
            joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_trad_id"))
    private List<ActivityNew> activityNewsList = new ArrayList<>();
}