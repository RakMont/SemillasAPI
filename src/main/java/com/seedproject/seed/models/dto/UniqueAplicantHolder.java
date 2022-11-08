package com.seedproject.seed.models.dto;


import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UniqueAplicantHolder {
    private Contributor contributor;
    private Long contribution_amount;
    private PaymentMethod paymentMethod;
    private Boolean send_news;
    private SendNewsType sendNewsType;
    private Date date_contribution;
}
