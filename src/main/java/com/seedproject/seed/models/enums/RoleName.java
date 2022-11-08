package com.seedproject.seed.models.enums;

public enum RoleName {
    R_PRINCIPAL("Principal"),
    R_REGISTROS("Registros"),
    R_SEGUIMIENTOS("Seguimiento"),
    R_SOUVENIRS("Souvenirs");

    public String value;
    private RoleName(String value){
        this.value=value;
    }
}
