package com.seedproject.seed.services;

import com.seedproject.seed.models.entities.EnterpriseContribution;
import com.seedproject.seed.repositories.ContributionRepository;
import com.seedproject.seed.repositories.EnterpriseContributionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class EnterpriseContributionService {
    @Inject
    ContributionRepository contributionRepository;
    @Inject
    EnterpriseContributionRepository enterpriseContributionRepository;

    public EnterpriseContribution saveEnterpriseContribution(EnterpriseContribution enterpriseContribution){
        contributionRepository.save(enterpriseContribution.getContribution());
        return enterpriseContributionRepository.save(enterpriseContribution);
    }
}
