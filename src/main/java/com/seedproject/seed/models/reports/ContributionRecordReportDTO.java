package com.seedproject.seed.models.reports;

import com.seedproject.seed.models.dto.interfaces.ContributionReportDTO;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class ContributionRecordReportDTO  {
   private String nro;
   private String seed_name;
   private String contr_type;
   private String payment_date;
   private Long payment_amount;
   private Long extra_amount;
   private Long spent_amount;
   private String receipt_code;
   private Boolean valid_transaction;

   private String valid_tr;
   private String payment_method;


   public ContributionRecordReportDTO(String index, ContributionReportDTO contributionRecord, SimpleDateFormat format) {
      this.nro = index;
      this.seed_name = contributionRecord.getSeed_name();
      this.contr_type = contributionRecord.getContribution_key().toString();
      this.payment_date = format.format(contributionRecord.getPayment_date());
      this.payment_amount = contributionRecord.getPayment_amount();
      this.extra_amount = contributionRecord.getExtra_amount() != null ? Long.parseLong(contributionRecord.getExtra_amount()) : 0;
      this.spent_amount = contributionRecord.getSpent_amount() != null ? Long.parseLong(contributionRecord.getSpent_amount()) : 0 ;
      this.receipt_code = contributionRecord.getReceipt_code();
      this.valid_transaction = contributionRecord.getValid_transaction();
      this.payment_method = contributionRecord.getPayment_method().toString();
      this.valid_tr = contributionRecord.getValid_transaction() ? "Sí" : "No";
   }

   public ContributionRecordReportDTO() {
   }

   public String getNro() {
      return nro;
   }

   public void setNro(String nro) {
      this.nro = nro;
   }

   public String getSeed_name() {
      return seed_name;
   }

   public void setSeed_name(String seed_name) {
      this.seed_name = seed_name;
   }

   public String getContr_type() {
      return contr_type;
   }

   public void setContr_type(String contr_type) {
      this.contr_type = contr_type;
   }

   public String getPayment_date() {
      return payment_date;
   }

   public void setPayment_date(String payment_date) {
      this.payment_date = payment_date;
   }

   public Long getPayment_amount() {
      return payment_amount;
   }

   public void setPayment_amount(Long payment_amount) {
      this.payment_amount = payment_amount;
   }

   public Long getExtra_amount() {
      return extra_amount;
   }

   public void setExtra_amount(Long extra_amount) {
      this.extra_amount = extra_amount;
   }

   public Long getSpent_amount() {
      return spent_amount;
   }

   public void setSpent_amount(Long spent_amount) {
      this.spent_amount = spent_amount;
   }

   public String getReceipt_code() {
      return receipt_code;
   }

   public void setReceipt_code(String receipt_code) {
      this.receipt_code = receipt_code;
   }

   public Boolean getValid_transaction() {
      return valid_transaction;
   }

   public void setReceipt_number(Boolean valid_transaction) {
      this.valid_transaction = valid_transaction;
   }

   public String getPayment_method() {
      return payment_method;
   }

   public void setPayment_method(String payment_method) {
      this.payment_method = payment_method;
   }
}
