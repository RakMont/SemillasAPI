package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.RemainderType;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConstantAplicantHolder {
    private Contributor contributor;
    private Long contribution_amount;
    private PaymentMethod paymentMethod;
    private SendNewsType sendNewsType;
    private Boolean send_news;
    private String beginMonth;
    private String paymentDay;
    private RemainderType reminderMethod;
}
