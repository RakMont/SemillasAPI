package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seedproject.seed.models.enums.AchievementSection;
import com.seedproject.seed.models.enums.TranslateLen;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
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

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "description", length=3000)
    private String description;

    @Column(name = "section")
    private AchievementSection section;

    @Enumerated(EnumType.STRING)
    @Column(name = "len")
    private TranslateLen len;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "register_date")
    private Date registerDate;
    @Column(name = "isTranslate")
    private Boolean isTranslate;

    @NotNull(message = "The volunteer must not be null")
    @ManyToOne()
    @JoinColumn(name = "reg_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter regVolunteer;


    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "activity_new_translates",
            joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_trad_id"))
    private List<ActivityNew> activityNewsList = new ArrayList<>();
}
