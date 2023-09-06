package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.ContributionRecordDao;
import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.filters.ContributionRecordFilter;
import com.seedproject.seed.services.ConstantContributionService;
import com.seedproject.seed.services.ContributionConfigService;
import com.seedproject.seed.services.ContributionRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/contribution")
public class ContributionController {
    @Inject
    ConstantContributionService constantContributionService;
    @Inject
    ContributionConfigService contributionConfigService;

    @Inject
    ContributionRecordService contributionRecordService;

    @GetMapping(path = {"/getContributionConfigById"})
    public ContributionConfigDTO findContributionConfigById(@RequestParam(required = true) String id){
        ContributionConfigDTO contributionConfig=contributionConfigService.getContributionConfigById(id);
        return contributionConfig;
    }

    @GetMapping(path = {"/getContributionRecordById"})
    public ContributionRecordDTO findContributionRecordById(@RequestParam(required = true) String id){
        ContributionRecordDTO contributionConfig=contributionRecordService.getContributionRecordById(id);
        return contributionConfig;
    }

    @PostMapping(value = "/createContributionRecord")
    public ResponseEntity<RequestResponseMessage> createContributionRecord(@RequestBody ContributionRecordDao contributionRecordDao) {
        return contributionRecordService.saveContributionRecord(contributionRecordDao);
    }

    @PostMapping(value = "/updateContributionRecord")
    public ResponseEntity<RequestResponseMessage> updateContributionRecord(@RequestBody ContributionRecordDao contributionRecordDao) {
        return contributionRecordService.updateContributionRecord(contributionRecordDao);
    }

    @GetMapping(path = {"/getRecords"})
    public Table getAcceptedSeeds(@RequestParam(required = false) String id){
        return contributionRecordService.getSeedContributionRecords(id);
    }

    @GetMapping(path = {"/getAllRecords"})
    public Table getAllContributionRecords(@Valid ContributionRecordFilter  contributionRecordFilter){
        return contributionRecordService.getAllDonations(contributionRecordFilter);
    }

    @GetMapping(path = {"/getExportRecords"})
    public Table getExportRecords(@Valid ContributionRecordFilter  contributionRecordFilter){
        return contributionRecordService.getExportRecords(contributionRecordFilter);
    }

    @PostMapping(value = "/deleteContributionRecord")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContributionRecord(@RequestBody String id) {
        contributionRecordService.deleteContributionRecord(id);
    }

}
