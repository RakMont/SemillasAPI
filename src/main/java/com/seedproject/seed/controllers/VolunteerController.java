package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.filters.VolunterFilter;
import com.seedproject.seed.services.RoleService;
import com.seedproject.seed.services.VolunterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/volunteers")
public class VolunteerController {
    @Inject
    VolunterService volunterService;

    @Inject
    RoleService roleService;

    @PostMapping(path = {"/create"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> createVolunteer(@RequestBody Volunter volunter)throws Exception {
        return volunterService.saveVolunter(volunter);
    }

    @PutMapping(path = {"/updateVolunter"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> updateVolunteer(@RequestBody Volunter volunter)throws Exception {
        return volunterService.updateVolunter(volunter);
    }

    @PostMapping(path = {"/updateVolunteerPassword"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> updateVolunteerPassword(Principal principal, @RequestBody VolunteerPasswordDTO volunteerPasswordDTO)throws Exception {
        return volunterService.updateVolunteerPassword(principal,volunteerPasswordDTO);
    }

    @GetMapping(path = {"/all"})
    public Table findAllVolunteers(@Valid VolunterFilter volunterFilter) {
        return volunterService.findAllVolunteer(volunterFilter);
    }

    @GetMapping(path = {"/getRoles"})
    public List<Role> findVolunteerRoles(@RequestBody String email) {
        Volunter volunter = volunterService.findVolunterRoles(email);
        return volunter.getRoles();
    }

    @GetMapping(path = {"/getVolunter"})
    public VolunterDTO getVolunteer(@RequestParam(required = true) String id, Principal principal) {
        return volunterService.findOneVolunteer(id, principal);
    }

    @PostMapping(value = "/deleteVolunter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<RequestResponseMessage> deleteVolunteerById(@RequestBody String id) {
        return volunterService.deleteVolunter(id);
    }

    @PostMapping(value = "/exitVolunter")
    public  ResponseEntity<RequestResponseMessage> exitVolunteerById(@RequestBody ExitPost exitPost) {
        return volunterService.exitVolunteer(exitPost);
    }

    @PostMapping(value = "/activateVolunter")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponseMessage> activateVolunter(@RequestBody ExitPost exitPost) {
        return volunterService.activateVolunteer(exitPost);
    }

    @GetMapping(path = {"/getExitMessages"})
    public List<ExitPost> getExitMessages(@RequestParam(required = true) String volunterId) {
        List<ExitPost> exitMessageList = volunterService.getExitMessages(volunterId);
        return exitMessageList;
    }

    @PutMapping(value = "return/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnVolunterById(@PathVariable Long id) {
        volunterService.getVolunterById(id);
    }

    @GetMapping(path = {"/trackingVolunteers"})
    public Table getTrackingVolunteers(){
        return volunterService.findAllTrackingVolunteers();
    }

    @GetMapping(path = {"/roles"})
    public List<Role> findAllApplicants(){
        return roleService.getRoles();
    }

    @GetMapping(path = {"/comboTrackingVolunteers"})
    public List<ComboVolunteer> findComboTrackingVolunteers(){
        return volunterService.findComboTrackingVolunteers();
    }
}
