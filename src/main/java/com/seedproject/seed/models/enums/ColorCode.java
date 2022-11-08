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
    STATE_PENDING("#e7d042"),
    STATE_ACEPTED("#6db669"),
    STATE_REJECTED("#bd5533"),
    ACCEPT_CONTR("#5c9254"),
    REJECT_CONTR("#db504a"),
    VIEW_CONTR("#ffe599"),
    VIEW_ASSIGNED_SEEDS("#88cc77"),
    ASSIGN_SEED("#dead22"),
    VIEW_TRACKING_SEEDS("#665916"),
    UNIQUE_CONTRIBUTION("#4f6485"),
    CONSTANT_CONTRIBUTION("#325132");
    public String value;
    private ColorCode(String value){
        this.value=value;
    }
}
