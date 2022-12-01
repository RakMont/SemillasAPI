package com.seedproject.seed.models.filters;

import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContributionRecordFilter {

    private Date beginDate;

    private Date endDate;

    private PaymentMethod paymentMethod;

    private String seedId;

    private ContributionType contributionType;
}
