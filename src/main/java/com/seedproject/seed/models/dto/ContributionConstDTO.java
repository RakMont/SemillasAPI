package com.seedproject.seed.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seedproject.seed.models.entities.ConstantContribution;
import com.seedproject.seed.models.enums.PaymentDate;
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
    public Long contributionAmount;
    public PaymentMethod paymentMethod;
    public Boolean sendNews;
    public SendNewsType sendNewsType;
    public String const_contribution_id;
    public String startMonth;
    public PaymentDate paymentNumberDay;
    public RemainderType remainderType;
    //@JsonFormat(pattern="yyyy-MM-dd")
    public Date contributionStartDate;
    //@JsonFormat(pattern="yyyy-MM-dd")
    public Date contributionEndDate;

    public ContributionConstDTO(ConstantContribution constantContribution) {
        this.contribution_id = constantContribution.getContribution().getContribution_id();
        this.contributionAmount = constantContribution.getContribution().getContribution_amount();
        this.paymentMethod = constantContribution.getContribution().getPaymentMethod();
        this.sendNews = constantContribution.getContribution().getSend_news();
        this.sendNewsType = constantContribution.getContribution().getSendNewsType();
        this.const_contribution_id = constantContribution.getConst_contribution_id().toString();
        this.startMonth = constantContribution.getStart_month();
        this.paymentNumberDay = constantContribution.getPaymentDate();
        this.remainderType = constantContribution.getRemainderType();
        this.contributionStartDate = constantContribution.getContributionStartDate();
        this.contributionEndDate = constantContribution.getContributionEndDate();
    }

    public ContributionConstDTO() {
    }
}
