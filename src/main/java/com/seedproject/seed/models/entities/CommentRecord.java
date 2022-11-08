package com.seedproject.seed.models.entities;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment_record")
@Getter
@Setter
public class CommentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_record_id;

    @NotNull
    @Column
    private String comment;

    @Column(name = "comment_date")
    private Date comment_date;
}