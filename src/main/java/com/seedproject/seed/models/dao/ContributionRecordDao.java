package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.enums.DonationMonth;
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
    Boolean validTransaction;
    String receipt_code;
    String extra_income_ammount;
    Boolean contribution_obtained;
    Boolean sent_payment_proof;
    String volunter_id;
    DonationMonth contributionMonth;
    /**/
    String extraExpenseId;
    Long extraExpenseAmount;
    String extraExpenseReason;
    Boolean hasExtraExpense;
}
