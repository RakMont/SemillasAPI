package com.seedproject.seed.models.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seedproject.seed.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contribution_record")
@Getter
@Setter
public class ContributionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contribution_record_id")
    private Long contribution_record_id;

    @NotNull(message = "The tracking assignment must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "tracking_assignment_id", referencedColumnName = "tracking_assignment_id")
    private TrackingAssignment trackingAssignment;

    @NotNull(message = "The contribution config must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contribution_config_id", referencedColumnName = "contribution_config_id")
    private ContributionConfig contributionConfig;


    @Column(name = "contributor_id")
    private Long contributor_id;

    @Column(name = "payment_date")
    private Date payment_date;

    @Column(name = "expected_payment_date")
    private Date expected_payment_date;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "contribution_ammount")
    private Long contribution_ammount;

    @Column(name = "receipt_number")
    private String receipt_number;

    @Column(name = "receipt_code")
    private String receipt_code;

    @Column(name = "extra_income_ammount")
    private String extra_income_ammount;

    @Column(name = "contribution_obtained")
    private Boolean contribution_obtained;

    @Column(name = "sent_payment_proof")
    private Boolean sent_payment_proof;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "extra_expense_id", referencedColumnName = "extra_expense_id")
    private ExtraExpense extraExpense;

    @NotNull(message = "The register volunter must not be null")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "register_volunter_id", referencedColumnName = "volunter_id")
    private Volunter volunter;

    @JsonIgnore
    @OneToMany /*podria ser many to many*/
    @NotNull(message = "The list of comments may not be empty")
    @JoinTable(name = "contribution_record_comment",
            joinColumns = @JoinColumn(name = "contribution_record_id", referencedColumnName = "contribution_record_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_record_id"))
    private List<CommentRecord> contributionRecordComments = new ArrayList<>();

}