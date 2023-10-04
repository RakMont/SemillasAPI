package com.seedproject.seed.models.dto;

import com.seedproject.seed.models.entities.SeedSouvenirTracking;
import com.seedproject.seed.models.enums.TrackingStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SeedSouvenirTrackingDTO {
    private String seedSouvenirTrackingId;
    private Date selectedDate;
    private Date souvenirSendDate;
    private TrackingStatus trackingStatus;
    private String trackingStatusLabel;
    private int spentAmount;
    private String chosenCity;
    private String observation;
    private String benefitedContributorId;
    private ComboSeed benefitedContributorLabel;
    private String volunteerInChargeId;
    private ComboVolunteer volunteerInChargeLabel;

    public SeedSouvenirTrackingDTO() {
    }
    public SeedSouvenirTrackingDTO(SeedSouvenirTracking seedSouvenirTracking) {
        this.seedSouvenirTrackingId = seedSouvenirTracking.getSeedSouvenirTrackingId().toString();
        this.selectedDate = seedSouvenirTracking.getSelectedDate();
        this.souvenirSendDate = seedSouvenirTracking.getSouvenirSendDate();
        this.trackingStatus = seedSouvenirTracking.getTrackingStatus();
        this.trackingStatusLabel = seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_SENT) ?
        "Enviado" : seedSouvenirTracking.getTrackingStatus().equals(TrackingStatus.SOUVENIR_PENDING) ? "Pendiente"
        : "Entregado";
        this.spentAmount = seedSouvenirTracking.getSpentAmount();
        this.chosenCity = seedSouvenirTracking.getChosenCity();
        this.observation = seedSouvenirTracking.getObservation();
        this.benefitedContributorId = seedSouvenirTracking.getBenefitedContributor().getContributor_id().toString();
        //this.benefitedContributorLabel = seedSouvenirTracking.getBenefitedContributor().getSeedFullName();
        this.volunteerInChargeId = seedSouvenirTracking.getVolunteerInCharge().getVolunterId().toString();
        //this.volunteerInChargeLabel = seedSouvenirTracking.getVolunteerInCharge().getFullName();
    }
}
