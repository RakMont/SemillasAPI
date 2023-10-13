package com.seedproject.seed.models.dto;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seedproject.seed.models.entities.ActivityNew;
import com.seedproject.seed.models.enums.TranslateLen;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityNewDTO {
    private String activityId;
    private String title;
    private String description;
    private TranslateLen len;
    private String imageLink;
    private Date registerDate;
    //private String regVolunteerId;
    private String regVolunteerName;
    private List<ActivityNewDTO> activityTranslates= new ArrayList<>();

    public ActivityNewDTO() {
    }
    public ActivityNewDTO(String activityId, ActivityNew activityNew) {
        this.activityId =activityId;
        this.title = activityNew.getTitle();
        this.description = activityNew.getDescription();
        this.len = activityNew.getLen();
        this.imageLink = activityNew.getImageLink();
        this.registerDate = activityNew.getRegisterDate();
        this.regVolunteerName = activityNew.getRegVolunteer().getFullName();
    }
    public ActivityNew getActivity(ActivityNewDTO activityNewDTO){
        ActivityNew res = new ActivityNew();
        res.setActivityId(activityNewDTO.getActivityId()!= null ?
                Long.parseLong(activityNewDTO.getActivityId()) : null);
        res.setTitle(activityNewDTO.getTitle());
        res.setDescription(activityNewDTO.getDescription());
        res.setLen(activityNewDTO.getLen());
        res.setImageLink(activityNewDTO.getImageLink());
        res.setRegisterDate(activityNewDTO.getRegisterDate());
        return res;
    }
}