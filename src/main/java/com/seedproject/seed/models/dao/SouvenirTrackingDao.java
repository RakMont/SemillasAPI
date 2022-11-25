package com.seedproject.seed.models.dao;

import com.seedproject.seed.models.entities.CommentRecord;
import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SouvenirTrackingDao {
    private String benefited_collaborator_id;
    private Date selected_date;
    private String observation;
    private String city;
    private String contributorId;
    private String volunteerInChargeId;

    /* ***************Tracking data*********************************** */
    private String souvenirTrackingId;
    private Date souvenir_send_date;
    private TrackingStatus trackingStatus;
    private String benefitedCollaboratorId;
    private int spentAmount;
    /* *********************Tracking coments**********************************************/
    List<CommentRecordDao> souvenirTrackingComments;
}
