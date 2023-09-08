package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.UniqueAplicantHolderDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.filters.ContributorFilter;
import com.seedproject.seed.models.filters.SeedFilter;
import com.seedproject.seed.services.ContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/applicants")
public class ContributorController {
    @Inject
    ContributorService contributorService;

    @PostMapping(value = "/unique", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> createUniqueApplicant(Principal principal,@RequestBody UniqueAplicantHolderDao uniqueAplicantHolderDao) {
        return contributorService.savUniqueContributor(principal,uniqueAplicantHolderDao);
    }

    @PostMapping(value = "/constant", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> createConstantApplicant(Principal principal,@RequestBody ConstantApplicantHolder constantApplicantHolder) {
        return contributorService.saveConstantContributor(principal, constantApplicantHolder);
    }

    @PostMapping(value = "/processSeed", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> processAplicant(@RequestBody ProcessSeedDTO processSeedDTO) {
        return contributorService.acceptApplicant(processSeedDTO);
    }
    @PostMapping(value = "/deactivateContributor", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<RequestResponseMessage> deactivateContributor(Principal principal, @RequestBody DeactivateContributorDTO deactivateContributorDTO) {
        return contributorService.deactivateContributor(principal, deactivateContributorDTO);
    }

    @GetMapping(path = {"/contributorSeeds/{contributorFilter}"})
    public List<ContributorDTO> findContributorSeeds(@PathVariable ContributorFilter contributorFilter){
        return contributorService.findAcceptedApplicants();
    }

    @GetMapping(path = {"/processed"})
    public Table getAcceptedSeeds(@Valid SeedFilter volunteerFilter){
        return contributorService.findAcceptedSeeds(volunteerFilter);
    }

    @GetMapping(path = {"/pending"})
    public Table getAllApplicants(){
        return contributorService.findAllPendingSeeds();
    }

    @GetMapping(path = {"/rejected"})
    public Table getRejectedSeeds(){
        return contributorService.findRejectedSeeds();
    }

    @GetMapping(path = {"/all"})
    public Table findAllAApplicants(@Valid SeedFilter volunterFilter) {
        return contributorService.findAllApplicants(volunterFilter);
    }


    @GetMapping(path = {"/activeseeds"})
    public List<ComboSeed> findActiveSeeds(){
        return contributorService.findActiveSeeds();
    }

    @GetMapping(path = {"/getSeedById"})
    public ContributorDTO getSeedById(@RequestParam(required = true) String id) {
        ContributorDTO contributor = contributorService.getSeedById(id);
        return contributor;
    }
}

