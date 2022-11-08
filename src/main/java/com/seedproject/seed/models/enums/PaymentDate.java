package com.seedproject.seed.models.enums;

public enum PaymentDate {
    CADA_5_DEL_MES(1),
    CADA_10_DEL_MES(2),
    CADA_15_DEL_MES(3),
    CADA_20_DEL_MES(4),
    CADA_25_DEL_MES(5);
    public int value ;
    private PaymentDate(int value) {
        this.value=value;
    }
}
