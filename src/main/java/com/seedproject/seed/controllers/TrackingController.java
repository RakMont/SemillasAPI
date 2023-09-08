package com.seedproject.seed.controllers;


import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.dao.TrackingAssignmentDao;
import com.seedproject.seed.models.filters.ContributorFilter;
import com.seedproject.seed.models.filters.SeedFilter;
import com.seedproject.seed.services.TrackingAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/trackingassignment")
public class TrackingController {
    @Inject
    TrackingAssignmentService trackingAssignmentService;

    @GetMapping(path = {"/trackingSeeds"})
    public Table getVolunteerTrackingSeeds(@RequestParam(required = true) String id){
        return trackingAssignmentService.getVolunteerTrackingSeeds(id);
    }

    @GetMapping(path = {"/volunteerTrackingSeeds"})
    public Table volunteerTrackingSeeds(Principal principal,
                                        @Valid SeedFilter seedFilter) {
        return trackingAssignmentService.findVolunteerTrackingSeeds(principal, seedFilter);
    }
    @GetMapping(path = {"/getAllTrackingSeeds"})
    public Table getAllTrackingSeeds(@RequestParam(required = false) ContributorFilter contributorFilter){
        return trackingAssignmentService.getAllTrackingSeeds(contributorFilter);
    }

    @PostMapping(path = {"/createAssinment"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> createAssignment(@RequestBody TrackingAssignmentDao trackingAssignment) {
       return trackingAssignmentService.saveTrackingAssignment(trackingAssignment);
    }
}
