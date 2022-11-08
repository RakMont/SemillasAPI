package com.seedproject.seed.services;

import com.seedproject.seed.models.entities.UniqueContribution;
import com.seedproject.seed.repositories.ContributionRepository;
import com.seedproject.seed.repositories.UniqueContributionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UniqueContributionService {
    @Inject
    ContributionRepository contributionRepository;
    @Inject
    UniqueContributionRepository uniqueContributionRepository;

    public UniqueContribution saveUniqueContribution(UniqueContribution uniqueContribution){
        contributionRepository.save(uniqueContribution.getContribution());
        return uniqueContributionRepository.save(uniqueContribution);
    }

    public UniqueContribution findUniqueContributionById(Long uniqueContributionId){
        UniqueContribution uniqueContribution=uniqueContributionRepository.getById(uniqueContributionId);
        return uniqueContribution;
    }
}