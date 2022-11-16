package com.seedproject.seed.controllers;


import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.dto.TrackingAssignmentDao;
import com.seedproject.seed.models.entities.TrackingAssignment;
import com.seedproject.seed.models.filters.ContributorFilter;
import com.seedproject.seed.services.TrackingAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/trackingassignment")
public class TrackingController {
    @Inject
    TrackingAssignmentService trackingAssignmentService;

    @GetMapping(path = {"/trackingSeeds"})
    public Table getVolunterTrackingSeeds(@RequestParam(required = true) String id){
        return trackingAssignmentService.getVolunterTrackingSeeds(id);
    }

    @GetMapping(path = {"/getAllTrackingSeeds"})
    public Table getAllTrackingSeeds(@RequestParam(required = false) ContributorFilter contributorFilter){
        return trackingAssignmentService.getAllTrackingSeeds(contributorFilter);
    }

    @PostMapping(path = {"/createAssinment"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createUniqueApplicant(@RequestBody TrackingAssignmentDao trackingAssignment) {
        trackingAssignmentService.saveTrackingAssignment(trackingAssignment);
    }
}
