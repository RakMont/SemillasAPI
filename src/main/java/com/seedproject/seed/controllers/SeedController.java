package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dao.EnterpriseApplicantHolderDao;
import com.seedproject.seed.models.dao.UniqueApplicantHolderDao;
import com.seedproject.seed.models.dao.ConstantApplicantHolder;
import com.seedproject.seed.models.dto.RequestResponseMessage;
import com.seedproject.seed.services.ContributorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/seeds/confirmed")
public class SeedController {
    @Inject
    ContributorService contributorService;

    @PostMapping(value = "/updateUnique", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> createUniqueApplicant(@RequestBody UniqueApplicantHolderDao uniqueApplicantHolderDao) {
        return contributorService.updateUniqueContributor(uniqueApplicantHolderDao);
    }

    @PostMapping(value = "/updateEnterprise", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> updateEnterpriseApplicant(@RequestBody EnterpriseApplicantHolderDao enterpriseApplicantHolderDao) {
        return contributorService.updateEnterpriseContributor(enterpriseApplicantHolderDao);
    }

    @PostMapping(value = "/updateConstant", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestResponseMessage> createConstantApplicant(@RequestBody ConstantApplicantHolder constantApplicantHolder) {
        return contributorService.updateConstantContributor(constantApplicantHolder);
    }
}
