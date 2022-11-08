package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dto.ExitPost;
import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.dto.VolunterDTO;
import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.models.filters.VolunterFilter;
import com.seedproject.seed.services.RoleService;
import com.seedproject.seed.services.VolunterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/volunters")
public class VolunterController {
    @Inject
    VolunterService volunterService;

    @Inject
    RoleService roleService;

    @PostMapping(path = {"/create"})
    @ResponseStatus(HttpStatus.CREATED)
    public void createVolunter(@RequestBody Volunter volunter)throws Exception {
        System.out.println("llego aqui" + volunter);
        volunterService.saveVolunter(volunter);
    }

    @PutMapping(path = {"/updateVolunter"})
    @ResponseStatus(HttpStatus.CREATED)
    public void updateVolunter(@RequestBody Volunter volunter) {
        volunterService.updateVolunter(volunter);
    }

    @GetMapping(path = {"/all"})
    public Table findAllVolunters() {
        return volunterService.findAllVolunter();
    }

    @GetMapping(path = {"/getRoles"})
    public List<Role> findVolunterRoles(@RequestBody String email) {
        List<Role> roles = new ArrayList<>();
        Volunter volunter = volunterService.findVolunterRoles(email);
        roles = volunter.getRoles();
        System.out.println("roles controller " + roles.size());
        return roles;
    }

    @GetMapping(path = {"/exitvolunters"})
    public Table findAllExitvolunters(@RequestParam(required = false) VolunterFilter volunterFilter) {
        Table voluntersDTOS = volunterService.findVoluntersByFilter(volunterFilter);
        return voluntersDTOS;
    }

    @GetMapping(path = {"/getVolunter"})
    public VolunterDTO listarId(@RequestParam(required = true) Long id) {
        VolunterDTO volunterDTO = new VolunterDTO(volunterService.findOneVolunter(id));
        return volunterDTO;
    }

    @PostMapping(value = "/deleteVolunter")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVolunterById(@RequestBody Long id) {
        volunterService.deleteVolunter(id);
    }

    @PostMapping(value = "/exitVolunter")
    @ResponseStatus(HttpStatus.CREATED)
    public void exitVolunterById(@RequestBody ExitPost exitPost) {
        volunterService.exitVolunter(exitPost.getId());
    }

    @PutMapping(value = "return/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnVolunterById(@PathVariable Long id) {
        volunterService.getVolunterById(id);
    }

    @GetMapping(path = {"/trackingVolunters"})
    public Table gettrackingVolunters(){
        return volunterService.findAlltrackingVolunters();
    }

    @GetMapping(path = {"/roles"})
    public List<Role> findAllApplicants(){
        return roleService.getRoles();
    }
}
