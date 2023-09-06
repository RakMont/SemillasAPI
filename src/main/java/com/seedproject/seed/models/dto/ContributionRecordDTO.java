package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.entities.ExtraExpense;
import com.seedproject.seed.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionRecordDTO {
    public String contributionRecordId;
    public Long contributionAmount;
    public Boolean contributionObtained;
    public ContributorDTO contributorDTO;
    public Date expectedPaymentDate;
    public String extraIncomeAmount;
    public PaymentMethod paymentMethod;
    public Date paymentDate;
    public String receiptCode;
    public String receiptNumber;
    public Boolean sentPaymentProof;
    public ExtraExpense extraExpense;
    public String trackingAssignmentId;
    public String contributionConfigId;

    public ContributionRecordDTO(ContributionRecord contributionRecord){
        this.contributionRecordId = contributionRecord.getContribution_record_id().toString();
        this.contributionAmount =contributionRecord.getContribution_ammount();
        this.contributionObtained = contributionRecord.getContribution_obtained();
        this.contributorDTO = new ContributorDTO(contributionRecord.getContributor());
        this.expectedPaymentDate = contributionRecord.getExpected_payment_date();
        this.extraIncomeAmount = contributionRecord.getExtra_income_ammount();
        this.paymentMethod = contributionRecord.getPaymentMethod();
        this.paymentDate = contributionRecord.getPayment_date();
        this.receiptCode = contributionRecord.getReceipt_code();
        this.receiptNumber = contributionRecord.getReceipt_number();
        this.sentPaymentProof = contributionRecord.getSent_payment_proof();
        this.trackingAssignmentId = contributionRecord.getTrackingAssignment().getTracking_assignment_id().toString();
        this.contributionConfigId = contributionRecord.getContributionConfig().getContribution_config_id().toString();
    }
}
