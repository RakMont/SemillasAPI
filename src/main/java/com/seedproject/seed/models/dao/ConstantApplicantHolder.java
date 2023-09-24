package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.enums.PaymentDate;
import com.seedproject.seed.models.enums.PaymentMethod;
import com.seedproject.seed.models.enums.RemainderType;
import com.seedproject.seed.models.enums.SendNewsType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConstantApplicantHolder {
    private String contributorId;
    private Contributor contributor;
    private Long contribution_amount;
    private PaymentMethod paymentMethod;
    private SendNewsType sendNewsType;
    private Boolean send_news;
    private String beginMonth;
    private PaymentDate paymentDay;
    private RemainderType reminderMethod;
}
