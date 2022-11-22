package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.UniqueAplicantHolderDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ResponseMessage;
import com.seedproject.seed.models.filters.ContributorFilter;
import com.seedproject.seed.services.ContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/applicants")
public class ContributorController {
    @Inject
    ContributorService contributorService;

    @PostMapping(value = "/unique", consumes = "application/json", produces = "application/json")
    public ResponseMessage createUniqueApplicant(@RequestBody UniqueAplicantHolderDao uniqueAplicantHolderDao) {
        return contributorService.savUniqueContributtor(uniqueAplicantHolderDao);
    }

    @PostMapping(value = "/constant", consumes = "application/json", produces = "application/json")
    public ResponseMessage createConstantApplicant(@RequestBody ConstantAplicantHolder constantAplicantHolder) {
        return contributorService.saveConstantContributtor(constantAplicantHolder);
    }

    @PostMapping(value = "/processSeed", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseMessage processAplicant(@RequestBody ProcessSeedDTO processSeedDTO) {
        return contributorService.acceptApplicant(processSeedDTO);
    }

    @GetMapping(path = {"/contributorSeeds/{contributorFilter}"})
    public List<ContributorDTO> findContributorSeeds(@PathVariable ContributorFilter contributorFilter){
        List<ContributorDTO> contributorDTOS= contributorService.findAcceptedApplicants();
        return contributorDTOS;
    }

    @GetMapping(path = {"/acepted"})
    public Table getAceptedSeeds(){
        return contributorService.findAceptedSeeds();
    }

    @GetMapping(path = {"/pending"})
    public Table getAllApplicants(){
        return contributorService.findAllPendingSeeds();
    }

    @GetMapping(path = {"/rejected"})
    public Table getRejectedSeeds(){
        return contributorService.findRejectedSeeds();
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

