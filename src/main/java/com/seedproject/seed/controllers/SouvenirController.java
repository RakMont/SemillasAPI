package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.SeedSouvenirTrackingDao;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.dto.SeedSouvenirTrackingDTO;
import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.filters.SouvenirTrackingFilter;
import com.seedproject.seed.services.SouvenirService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/souvenirs")
public class SouvenirController {

    @Inject
    SouvenirService seedSouvenirTrackingService;
    /*
    @PostMapping(value = "/createSouvenirTracking", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> createSouvenirTracking(@RequestBody SouvenirTrackingDao souvenirTrackingDao) {
        return souvenirService.createSouvenirTracking(souvenirTrackingDao);
    }
    */
    /////////////////////////////////////////////////////////////////////////////////////

    @GetMapping(path = {"/getAll"})
    public Table getAll(@Valid SouvenirTrackingFilter souvenirTrackingFilter){
        return seedSouvenirTrackingService.findAllSouvenirsTracking(souvenirTrackingFilter);
    }

    @PostMapping(value = "/createSeedSouvenirTracking", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> createSeedSouvenirTracking(Principal principal, @RequestBody SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        return seedSouvenirTrackingService.createSeedSouvenirTracking(principal, seedSouvenirTrackingDao);
    }

    @PostMapping(value = "/updateSeedSouvenirTracking", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> updateSeedSouvenirTracking(Principal principal, @RequestBody SeedSouvenirTrackingDao seedSouvenirTrackingDao) {
        return seedSouvenirTrackingService.updateSeedSouvenirTracking(principal, seedSouvenirTrackingDao);
    }

    @PostMapping(value = "/deleteSeedSouvenirTracking")
    public ResponseEntity<RequestResponseMessage> deleteSeedSouvenirTracking(@RequestBody String id) {
        return seedSouvenirTrackingService.deleteSeedSouvenirTracking(id);
    }

    @GetMapping(path = {"/getSeedSouvenirTracking"})
    public SeedSouvenirTrackingDTO getSeedSouvenirTracking(@RequestParam(required = true) String id){
        return seedSouvenirTrackingService.getSeedSouvenirTracking(id);
    }

}
