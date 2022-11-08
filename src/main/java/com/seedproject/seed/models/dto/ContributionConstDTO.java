package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.ConstantContribution;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.RemainderType;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionConstDTO implements ContributionDTO {
    public Long contribution_id;
    public Long contribution_amount;
    public PaymentMethod paymentMethod;
    public Boolean send_news;
    public SendNewsType sendNewsType;
    public String const_contribution_id;
    public String start_month;
    public int paymentDate;
    public RemainderType remainderType;
    public Date contributionStartDate;
    public Date contributionEndDate;

    public ContributionConstDTO(ConstantContribution constantContribution) {
        this.contribution_id = constantContribution.getContribution().getContribution_id();
        this.contribution_amount = constantContribution.getContribution().getContribution_amount();
        this.paymentMethod = constantContribution.getContribution().getPaymentMethod();
        this.send_news = constantContribution.getContribution().getSend_news();
        this.sendNewsType = constantContribution.getContribution().getSendNewsType();
        this.const_contribution_id = constantContribution.getConst_contribution_id().toString();
        this.start_month = constantContribution.getStart_month();
        this.paymentDate = constantContribution.getPaymentDate().value;
        this.remainderType = constantContribution.getRemainderType();
        this.contributionStartDate = constantContribution.getContributionStartDate();
        this.contributionEndDate = constantContribution.getContributionEndDate();
    }

    public ContributionConstDTO() {
    }
}
