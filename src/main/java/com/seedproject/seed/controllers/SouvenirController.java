package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SouvenirTrackingDao;
import com.seedproject.seed.models.dto.ProcessSeedDTO;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.services.SouvenirService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/souvenir")
public class SouvenirController {

    @Inject
    SouvenirService souvenirService;

    @PostMapping(value = "/createBenefitedSeed", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> createBenefitedCollaborator(@RequestBody SouvenirTrackingDao souvenirTrackingDao) {
        return souvenirService.createBenefitedCollaborator(souvenirTrackingDao);
    }

    @PostMapping(value = "/createSouvenirTracking", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> createSouvenirTracking(@RequestBody SouvenirTrackingDao souvenirTrackingDao) {
        return souvenirService.createSouvenirTracking(souvenirTrackingDao);
    }

}
