package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.enums.DonationMonth;
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
    public String paymentMethodLabel;
    public Date paymentDate;
    public String receiptCode;
    public Boolean validTransaction;
    public Boolean sentPaymentProof;
    public Long extraExpenseAmount;
    public String extraExpenseReason;
    public DonationMonth contributionMonth;
    public Boolean hasExtraExpense;
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
        this.validTransaction = contributionRecord.getValidTransaction();
        this.contributionMonth = contributionRecord.getContributionMonth();
        this.sentPaymentProof = contributionRecord.getSent_payment_proof();
        this.trackingAssignmentId = contributionRecord.getTrackingAssignment().getTracking_assignment_id().toString();
        this.contributionConfigId = contributionRecord.getContributionConfig().getContribution_config_id().toString();
        if (contributionRecord.getExtraExpense()!= null){
            this.extraExpenseAmount = contributionRecord.getExtraExpense().getExtra_expense_amount();
            this.extraExpenseReason = contributionRecord.getExtraExpense().getExtra_expense_reason();
            this.hasExtraExpense=true;
        }
        this.paymentMethodLabel =
                contributionRecord.getPaymentMethod().equals(PaymentMethod.CODIGO_QR) ? "Codigo QR":
                contributionRecord.getPaymentMethod().equals(PaymentMethod.DEPOSITO_BANCARIO) ? "Deposito Bancario" :
                contributionRecord.getPaymentMethod().equals(PaymentMethod.TRANSFERENCIA_BANCARIA) ? "Transferencia Bancaria" :
                        contributionRecord.getPaymentMethod().equals(PaymentMethod.EFECTIVO) ? "Efectivo" : "";
    }
}
