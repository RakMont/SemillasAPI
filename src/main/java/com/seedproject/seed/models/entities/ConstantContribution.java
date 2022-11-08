package com.seedproject.seed.models.entities;

import com.seedproject.seed.models.enums.PaymentDate;
import com.seedproject.seed.models.enums.RemainderType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "constant_contribution")
@Getter
@Setter
public class ConstantContribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "const_contribution_id")
    private Long const_contribution_id;

    @Column(name = "start_month")
    private String start_month;

    @Column(name = "payment_date")
    private PaymentDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "remainder_type")
    private RemainderType remainderType;

    @Column(name = "contribution_start_date")
    private Date contributionStartDate;

    @Column(name = "contribution_end_date")
    private Date contributionEndDate;

    @NotNull(message = "The contribution must not be null")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contribution_id", referencedColumnName = "contribution_id")
    private Contribution contribution;
}