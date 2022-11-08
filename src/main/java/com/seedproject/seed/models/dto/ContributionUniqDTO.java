package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.UniqueContribution;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionUniqDTO implements ContributionDTO{
    public Long contribution_id;
    public Long contribution_amount;
    public PaymentMethod paymentMethod;
    public Boolean send_news;
    public SendNewsType sendNewsType;

    public String unique_contribution_id;
    public Date date_contribution;

    public ContributionUniqDTO(UniqueContribution uniqueContribution) {
        this.contribution_id = uniqueContribution.getContribution().getContribution_id();
        this.contribution_amount = uniqueContribution.getContribution().getContribution_amount();
        this.paymentMethod = uniqueContribution.getContribution().getPaymentMethod();
        this.send_news = uniqueContribution.getContribution().getSend_news();
        this.sendNewsType = uniqueContribution.getContribution().getSendNewsType();

        this.unique_contribution_id = uniqueContribution.getUnique_contribution_id().toString();
        this.date_contribution = uniqueContribution.getDate_contribution();
    }
}
