package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "len", length=1000)
    private String len;

    @NotNull(message = "The volunteer must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reg_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter regVolunteer;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "contribution_record_comment",
            joinColumns = @JoinColumn(name = "contribution_record_id", referencedColumnName = "contribution_record_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_record_id"))
    private List<CommentRecord> contributionRecordComments = new ArrayList<>();

}
