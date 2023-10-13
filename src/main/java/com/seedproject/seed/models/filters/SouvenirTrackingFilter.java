package com.seedproject.seed.models.filters;

import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SouvenirTrackingFilter {
    private Date beginSelectedDate;

    private Date endSelectedDate;
    private TrackingStatus trackingStatus;
}
