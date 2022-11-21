package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ResponseMessage;
import com.seedproject.seed.services.ConstantContributionService;
import com.seedproject.seed.services.ContributionConfigService;
import com.seedproject.seed.services.ContributionRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

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

    @PostMapping(value = "/createContributionRecord")
    public ResponseEntity<RequestResponseMessage> createUniqueApplicant(@RequestBody ContributionRecordDao contributionRecordDao) {
        return contributionRecordService.saveContributionRecord(contributionRecordDao);
    }

    @PostMapping(value = "/updateContributionRecord")
    public void updateContributionRecord(@RequestBody ContributionRecordDao contributionRecordDao) {
        contributionRecordService.updateContributionRecord(contributionRecordDao);
    }

    @GetMapping(path = {"/getRecords"})
    public Table getAceptedSeeds(@RequestParam(required = false) String id){
        return contributionRecordService.getSeedContributionRecords(id);
    }

    @GetMapping(path = {"/getExportRecords"})
    public Table getExportRecords(@RequestParam(required = false) String id){
        return contributionRecordService.getSeedContributionRecords(id);
    }

    @PostMapping(value = "/deleteContributionRecord")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContributionRecord(@RequestBody String id) {
        contributionRecordService.deleteContributionRecord(id);
    }

}
