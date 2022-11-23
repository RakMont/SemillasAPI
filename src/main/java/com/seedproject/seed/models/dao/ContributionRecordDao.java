package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionRecordDao {
    String contribution_record_id;
    String tracking_assignment_id;
    String contribution_config_id;
    String contributor_id;
    Date payment_date;
    Date expected_payment_date;
    PaymentMethod paymentMethod;
    Long contribution_ammount;
    String receipt_number;
    String receipt_code;
    String extra_income_ammount;
    Boolean contribution_obtained;
    Boolean sent_payment_proof;
    String volunter_id;

    /* Extra expense */
    String extra_expense_id;
    Long extra_expense_amount;
    String extra_expense_reason;
    Date extra_expense_date;
}
