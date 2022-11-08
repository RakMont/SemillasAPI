package com.seedproject.seed.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "extra_expense")
@Getter
@Setter
public class ExtraExpense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extra_expense_id")
    private Long extra_expense_id;

    @Column(name = "extra_expense_amount")
    private Long extra_expense_amount;

    @Column(name = "extra_expense_reason")
    private String extra_expense_reason;

    @Column(name = "extra_expense_date")
    private Date extra_expense_date;
}
