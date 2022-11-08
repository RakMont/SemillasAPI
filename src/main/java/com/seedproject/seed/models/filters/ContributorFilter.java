package com.seedproject.seed.models.filters;

import lombok.Getter;

import java.util.Date;

@Getter
public class ContributorFilter {

    private Long state;

    private Date sendDate;

    private Date beginSendDate;

    private Date endSendDate;

    private String name;

    private Long dni;
}
