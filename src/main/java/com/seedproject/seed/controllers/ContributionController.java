package com.seedproject.seed.controllers;

import com.seedproject.seed.models.dto.ContributionConfigDTO;
import com.seedproject.seed.services.ConstantContributionService;
import com.seedproject.seed.services.ContributionConfigService;
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

    @GetMapping(path = {"/getContributionConfigById"})
    public ContributionConfigDTO findContributionConfigById(@RequestParam(required = true) Long id){
        ContributionConfigDTO contributionConfig=contributionConfigService.getContributionConfigById(id);
        return contributionConfig;
    }
}
