package com.seedproject.seed.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "processed_contributor")
@Getter
@Setter
public class ProcessedContributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processed_contributor_id")
    private Long processed_contributor_id;

    @Column(name = "processed_date")
    private Date processed_date;

    @Column(name = "process_reason")
    private String process_reason;

    @NotNull(message = "The process volunter must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "process_volunter_id", referencedColumnName = "volunter_id")
    private Volunter process_volunter;

    @NotNull(message = "The contributor must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contributor_id", referencedColumnName = "contributor_id")
    private Contributor contributor;

    @JsonIgnore
    @OneToMany
    @JoinTable(name = "process_contributor_comment",
            joinColumns = @JoinColumn(name = "processed_contributor_id", referencedColumnName = "processed_contributor_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_contributor_id"))
    private List<CommentRecord> come;
}