package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.models.enums.PaymentMethod;

import java.util.Date;

public interface ContributionReportDTO {
    Long getContribution_record_id();
    String getSeed_name();
    ContributionType getContribution_key();
    Date getPayment_date();
    Date getExpected_payment_date();
    Long getPayment_amount();
    String getExtra_amount();
    String getSpent_amount();
    String getReceipt_code();
    String getReceipt_number();
    PaymentMethod getPayment_method();

}
