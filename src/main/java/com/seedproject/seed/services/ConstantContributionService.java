package com.seedproject.seed.services;

import com.seedproject.seed.models.entities.ConstantContribution;
import com.seedproject.seed.repositories.ConstantContributionRepository;
import com.seedproject.seed.repositories.ContributionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ConstantContributionService {
    @Inject
    ContributionRepository contributionRepository;
    @Inject
    ConstantContributionRepository constantContributionRepository;

    public ConstantContribution saveConstantContribution(ConstantContribution constantContribution){
        contributionRepository.save(constantContribution.getContribution());
        return constantContributionRepository.save(constantContribution);
    }

    public ConstantContribution findConstantContributionById(Long constantContributionId){
        ConstantContribution constantContribution=constantContributionRepository.getById(constantContributionId);
        return constantContribution;
    }
}
