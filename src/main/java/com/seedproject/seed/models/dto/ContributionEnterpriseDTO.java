package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.dto.interfaces.ContributionDTO;
import com.seedproject.seed.models.entities.EnterpriseContribution;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionEnterpriseDTO implements ContributionDTO {
    public Long contribution_id;
    public Long contributionAmount;
    public PaymentMethod paymentMethod;
    public Boolean sendNews;
    public SendNewsType sendNewsType;

    public String enterprise_contribution_id;
    public Date dateContribution;

    public ContributionEnterpriseDTO(EnterpriseContribution enterpriseContribution) {
        this.contribution_id = enterpriseContribution.getContribution().getContribution_id();
        this.contributionAmount = enterpriseContribution.getContribution().getContribution_amount();
        this.paymentMethod = enterpriseContribution.getContribution().getPaymentMethod();
        this.sendNews = enterpriseContribution.getContribution().getSend_news();
        this.sendNewsType = enterpriseContribution.getContribution().getSendNewsType();

        this.enterprise_contribution_id = enterpriseContribution.getEnterprise_contribution_id().toString();
        this.dateContribution = enterpriseContribution.getDate_contribution();
    }
}
