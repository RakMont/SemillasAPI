package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SeedSouvenirTrackingDao {
    private String seedSouvenirTrackingId;
    private Date selectedDate;
    private Date souvenirSendDate;
    private TrackingStatus trackingStatus;
    private int spentAmount;
    private String chosenCity;
    private String observation;
    private String benefitedContributorId;
    private String volunteerInChargeId;
    //List<CommentRecord> comments = new ArrayList<>();
    /**/
}
