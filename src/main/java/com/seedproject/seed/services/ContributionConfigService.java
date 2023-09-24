package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.*;
import com.seedproject.seed.models.entities.ConstantContribution;
import com.seedproject.seed.models.entities.ContributionConfig;
import com.seedproject.seed.models.entities.EnterpriseContribution;
import com.seedproject.seed.models.entities.UniqueContribution;
import com.seedproject.seed.models.enums.ContributionType;
import com.seedproject.seed.repositories.ConstantContributionRepository;
import com.seedproject.seed.repositories.ContributionConfigRepository;
import com.seedproject.seed.repositories.UniqueContributionRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class ContributionConfigService {
    @Inject
    ContributionConfigRepository contributionConfigRepository;
    @Inject
    ConstantContributionRepository constantContributionRepository;
    @Inject
    UniqueContributionRepository uniqueContributionRepository;
    @Inject
    UniqueContributionService uniqueContributionService;
    @Inject
    EnterpriseContributionService enterpriseContributionService;
    @Inject
    EncripttionService encripttionService;
    public ContributionConfig saveUniqueContributionConfig(UniqueContribution uniqueContribution){
        UniqueContribution contribution=uniqueContributionService.saveUniqueContribution(uniqueContribution);
        ContributionConfig contributionConfig=new ContributionConfig();
        contributionConfig.setUniqueContribution(contribution);
        contributionConfig.setIs_active(true);
        contributionConfig.setContribution_key(ContributionType.APORTE_UNICO);
        return contributionConfigRepository.save(contributionConfig);
    }
    public ContributionConfig saveEnterpriseContributionConfig(EnterpriseContribution enterpriseContribution){
        EnterpriseContribution contribution=enterpriseContributionService.saveEnterpriseContribution(enterpriseContribution);
        ContributionConfig contributionConfig=new ContributionConfig();
        contributionConfig.setEnterpriseContribution(contribution);
        contributionConfig.setIs_active(true);
        contributionConfig.setContribution_key(ContributionType.APORTE_EMPRESAS);
        return contributionConfigRepository.save(contributionConfig);
    }
    public ContributionConfig saveConstantContributionConfig(ConstantContribution constantContribution){
        ConstantContribution contribution=constantContributionRepository.save(constantContribution);
        ContributionConfig contributionConfig=new ContributionConfig();
        contributionConfig.setConstantContribution(contribution);
        contributionConfig.setIs_active(true);
        contributionConfig.setContribution_key(ContributionType.APORTE_CONSTANTE);
        return contributionConfigRepository.save(contributionConfig);
    }

    public ContributionConfigDTO getContributionConfigById(String id){
        id = encripttionService.decrypt(id);
        Optional<ContributionConfig> contributionConfig = contributionConfigRepository.findById(Long.parseLong(id));

        ContributionConfigDTO contributionConfigDTO = new ContributionConfigDTO(contributionConfig.get());

        if (contributionConfig.get().getContribution_key().equals(ContributionType.APORTE_CONSTANTE)){
            ContributionDTO contributionDTO = new ContributionConstDTO(contributionConfig.get().getConstantContribution());
            contributionConfigDTO.setContribution(contributionDTO);

        }
        else{
            ContributionDTO contributionDTO = new ContributionUniqDTO(contributionConfig.get().getUniqueContribution());
            contributionConfigDTO.setContribution(contributionDTO);

        }
        return contributionConfigDTO;
    }


}