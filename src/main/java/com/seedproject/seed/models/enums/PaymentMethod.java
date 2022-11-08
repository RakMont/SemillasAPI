package com.seedproject.seed.models.enums;

public enum PaymentMethod {
    CODIGO_QR(1),
    DEPOSITO_BANCARIO(2),
    TRANSFERENCIA_BANCARIA(3),
    EFECTIVO(4);

    public int value;
    private PaymentMethod(int value){
        this.value=value;
    }
}
