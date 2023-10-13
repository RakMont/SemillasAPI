package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dto.ActivityNewDTO;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.services.ActivityNewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/activities")
public class ActivityController {
    @Inject
    ActivityNewService activityNewService;

    @PostMapping(path = {"/createActivity"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> createNewActivity(Principal principal, @RequestBody ActivityNewDTO activityNewDTO){
        return activityNewService.createNewActivity(principal, activityNewDTO);
    }

    @PostMapping(path = {"/updateActivity"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> updateActivity(Principal principal, @RequestBody ActivityNewDTO activityNewDTO){
        return activityNewService.updateActivity(principal, activityNewDTO);
    }

    @GetMapping(path = {"/getAllActivities"})
    public List<ActivityNewDTO> getAllActivities(){
        return activityNewService.getAllActivities();
    }

    @GetMapping(path = {"/getActivity"})
    public ActivityNewDTO getActivity(@RequestParam(required = true) String id){
        return activityNewService.getActivity(id);
    }

    @PostMapping(value = "/deleteActivity")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<RequestResponseMessage> deleteActivity(@RequestBody String id) {
        return activityNewService.deleteActivity(id);
    }
}
