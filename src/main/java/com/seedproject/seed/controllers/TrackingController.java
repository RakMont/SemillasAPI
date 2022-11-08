package com.seedproject.seed.controllers;


import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.entities.TrackingAssignment;
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
    public Table gettrackingSeeds(@RequestParam(required = true) Long id){
        return trackingAssignmentService.findtrackingSeeds(id);
    }

    @PostMapping(path = {"/createAssinment"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createUniqueApplicant(@RequestBody TrackingAssignment trackingAssignment) {
        trackingAssignmentService.saveTrackingAssignment(trackingAssignment);
    }
}
