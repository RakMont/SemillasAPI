package com.seedproject.seed.models.reports;

import com.seedproject.seed.models.entities.ContributionRecord;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.List;


public class ContributionRecordDataSource implements JRDataSource {
    private List<ContributionRecord> contributionRecords;
    private int index = 0;
    @Override
    public boolean next() throws JRException {
        index++;
        return (index<contributionRecords.size());
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        Object value = null;
        String fieldName = jrField.getName();
        switch (fieldName){
            case "nro":
                value = "0";
                break;

            case "seed_name":
                value = contributionRecords.get(index).getVolunter().getUser().getName();
                break;

            case "contr_type":
                value = contributionRecords.get(index).getContributionConfig().getContribution_key().toString();
                break;

            case "payment_date":
                value = contributionRecords.get(index).getPayment_date().toString();
                break;
            case "payment_amount":
                value = contributionRecords.get(index).getContribution_ammount().toString();
                break;
            case "extra_amount":
                value = contributionRecords.get(index).getExtra_income_ammount();
                break;
            case "spent_amount":
                value = "0";
                break;
            case "receipt_code":
                value = contributionRecords.get(index).getReceipt_code();
                break;
            case "receipt_number":
                value = contributionRecords.get(index).getReceipt_number();
                break;
            case "payment_method":
                value = contributionRecords.get(index).getPaymentMethod().toString();
                break;
        }
        return value;
    }

    public JRDataSource getDataSource(){
        //this.contributionRecords = contributionRecords;
        return new ContributionRecordDataSource();
    }
}