package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment_record")
@Getter
@Setter
public class CommentRecord{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_record_id;

    @NotNull
    @Column(name = "comment")
    private String comment;

    @Column(name = "comment_date")
    private Date comment_date;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_volunteer_id", referencedColumnName = "volunter_id")
    private Volunter registerVolunteer;

    //@JsonIgnore
   // @ManyToMany(mappedBy = "souvenirTrackingComments")
    //private List<SouvenirTracking> trackings;
}