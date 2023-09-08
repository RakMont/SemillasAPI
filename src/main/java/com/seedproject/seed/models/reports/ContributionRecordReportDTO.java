package com.seedproject.seed.models.reports;

import com.seedproject.seed.models.entities.ContributionRecord;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.List;


public class ContributionRecordReportDTO  {
   private String nro;
   private String seed_name;
   private String contr_type;
   private String payment_date;
   private String payment_amount;
   private String extra_amount;
   private String spent_amount;
   private String receipt_code;
   private String receipt_number;
   private String payment_method;


   public ContributionRecordReportDTO(String index, ContributionRecord contributionRecord) {
      this.nro = index;
      this.seed_name = contributionRecord.getVolunter().getUser().getName();
      this.contr_type = contributionRecord.getContributionConfig().getContribution_key().toString();
      this.payment_date = contributionRecord.getPayment_date().toString();
      this.payment_amount = contributionRecord.getContribution_ammount().toString();
      this.extra_amount = contributionRecord.getExtra_income_ammount();
      this.spent_amount = "0";
      this.receipt_code = contributionRecord.getReceipt_code();
      this.receipt_number = contributionRecord.getReceipt_number();
      this.payment_method = contributionRecord.getPaymentMethod().toString();
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

   public String getPayment_amount() {
      return payment_amount;
   }

   public void setPayment_amount(String payment_amount) {
      this.payment_amount = payment_amount;
   }

   public String getExtra_amount() {
      return extra_amount;
   }

   public void setExtra_amount(String extra_amount) {
      this.extra_amount = extra_amount;
   }

   public String getSpent_amount() {
      return spent_amount;
   }

   public void setSpent_amount(String spent_amount) {
      this.spent_amount = spent_amount;
   }

   public String getReceipt_code() {
      return receipt_code;
   }

   public void setReceipt_code(String receipt_code) {
      this.receipt_code = receipt_code;
   }

   public String getReceipt_number() {
      return receipt_number;
   }

   public void setReceipt_number(String receipt_number) {
      this.receipt_number = receipt_number;
   }

   public String getPayment_method() {
      return payment_method;
   }

   public void setPayment_method(String payment_method) {
      this.payment_method = payment_method;
   }
}
