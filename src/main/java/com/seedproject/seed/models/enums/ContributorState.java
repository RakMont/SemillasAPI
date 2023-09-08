package com.seedproject.seed.models.enums;

import lombok.Getter;

@Getter
public enum ContributorState {
    ACCEPTED(1),
    REJECTED(2),
    PENDING(3),
    PAUSED(4),
    DESERTER(5);

    public int value;
    private ContributorState(int value){
        this.value=value;
    }
}
