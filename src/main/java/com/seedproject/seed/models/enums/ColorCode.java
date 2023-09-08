package com.seedproject.seed.models.enums;

public enum ColorCode {
    EDIT("#8B9068"),
    DELETE("#DB4035"),
    VIEW("#AFB83B"),
    EDIT_ROLES( "FF9933"),
    R_PRINCIPAL_COLOR( "#4b597b"),
    R_SEGUIMIENTOS_COLOR( "#4b597b"),
    R_SOUVENIRS_COLOR( "#4b597b"),
    R_REGISTROS_COLOR("#4b597b"),
    STATE_PENDING("#f6ae2d"),
    STATE_PAUSED("#ce7e00"),
    STATE_ACEPTED("#6db669"),
    STATE_REJECTED("#bd5533"),
    ACCEPT_CONTR("#6b9074"),
    REJECT_CONTR("#db504a"),
    VIEW_CONTR("#084c61"),
    VIEW_ASSIGNED_SEEDS("#88cc77"),
    ASSIGN_SEED("#dead22"),
    VIEW_TRACKING_SEEDS("#f5ac32"),
    UNIQUE_CONTRIBUTION("#4f6485"),
    CONSTANT_CONTRIBUTION("#325132"),
    PAYMENT_METHODS("#6b9074"),
    EDIT_PASSWORD("#f18a5c");
    public String value;
    private ColorCode(String value){
        this.value=value;
    }
}
