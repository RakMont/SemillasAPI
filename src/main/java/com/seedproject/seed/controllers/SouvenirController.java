package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SouvenirTrackingDao;
import com.seedproject.seed.models.dto.ComboSeed;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.filters.SouvenirTrackingFilter;
import com.seedproject.seed.services.SouvenirService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

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

    @PostMapping(value = "/deleteBenefitedSeedById")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<RequestResponseMessage> deleteBenefitedCollaboratorById(@RequestBody String id) {
        return souvenirService.deleteBenefitedCollaboratorById(id);
    }

    @PostMapping(value = "/deleteSouvenirTrackById")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<RequestResponseMessage> deleteSouvenirTrackById(@RequestBody String id) {
        return souvenirService.deleteSouvenirTrackingById(id);
    }

    @PostMapping(value = "/createSouvenirTracking", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> createSouvenirTracking(@RequestBody SouvenirTrackingDao souvenirTrackingDao) {
        return souvenirService.createSouvenirTracking(souvenirTrackingDao);
    }

    @GetMapping(path = {"/getAllBenefitedSeeds"})
    public Table getAllBenefitedSeeds(@Valid SouvenirTrackingFilter souvenirTrackingFilter){
        return souvenirService.getAllBenefitedSeeds(souvenirTrackingFilter);
    }

    @GetMapping(path = {"/getAllSouvenirTracking"})
    public Table getAllSouvenirTracking(@Valid SouvenirTrackingFilter souvenirTrackingFilter){
        return souvenirService.getAllSouvenirTracking(souvenirTrackingFilter);
    }

    @GetMapping(path = {"/activeBenefitedSeeds"})
    public List<ComboSeed> findActiveSeeds(){
        return souvenirService.findBenefitedSeeds();
    }
}
