package com.seedproject.seed.services;

import com.seedproject.seed.models.dto.ContributionRecordDao;
import com.seedproject.seed.models.dto.Table;
import com.seedproject.seed.models.entities.ContributionConfig;
import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.entities.TrackingAssignment;
import com.seedproject.seed.models.entities.Volunter;
import com.seedproject.seed.repositories.ContributionConfigRepository;
import com.seedproject.seed.repositories.ContributionRecordRepository;
import com.seedproject.seed.repositories.TrackingAssignmentRepository;
import com.seedproject.seed.repositories.VolunterRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ContributionRecordService {
    @Inject
    ContributionRecordRepository contributionRecordRepository;
    @Inject
    TrackingAssignmentRepository trackingAssignmentRepository;
    @Inject
    ContributionConfigRepository contributionConfigRepository;
    @Inject
    VolunterRepository volunterRepository;
    @Inject
    EncripttionService encripttionService;

    public Table getSeedContributionRecords(String SeedId){
        Long id = Long.parseLong(encripttionService.decrypt(SeedId));
        List<ContributionRecord> contributionRecords = contributionRecordRepository.findAll();
        return this.getContributionsInformat(contributionRecords);
    }

    public Table getContributionsInformat(List<ContributionRecord> contributionRecords){
        return null;
    }

    public void saveContributionRecord(ContributionRecordDao contributionRecordDao){
        ContributionRecord contributionRecord = new ContributionRecord();

        TrackingAssignment trackingAssignment = trackingAssignmentRepository.getById(Long.parseLong(contributionRecordDao.getTracking_assignment_id()));
        ContributionConfig contributionConfig = contributionConfigRepository.getById(Long.parseLong(contributionRecordDao.getContribution_config_id()));
        Volunter volunter = volunterRepository.getById(Long.parseLong(contributionRecordDao.getVolunter_id()));
        contributionRecord.setContributor_id(Long.parseLong(contributionRecordDao.getContributor_id()));
        contributionRecord.setPayment_date(contributionRecordDao.getPayment_date());
        contributionRecord.setExpected_payment_date(contributionRecordDao.getExpected_payment_date());
        contributionRecord.setPaymentMethod(contributionRecordDao.getPaymentMethod());
        contributionRecord.setContribution_ammount(contributionRecordDao.getContribution_ammount());
        contributionRecord.setReceipt_number(contributionRecordDao.getReceipt_number());
        contributionRecord.setReceipt_code(contributionRecordDao.getReceipt_code());
        contributionRecord.setExtra_income_ammount(contributionRecordDao.getExtra_income_ammount());
        contributionRecord.setContribution_obtained(contributionRecordDao.getContribution_obtained());
        contributionRecord.setSent_payment_proof(contributionRecordDao.getSent_payment_proof());

        contributionRecord.setVolunter(volunter);
        contributionRecord.setContributionConfig(contributionConfig);
        contributionRecord.setTrackingAssignment(trackingAssignment);

        try {
            contributionRecordRepository.save(contributionRecord);
        }catch (Exception exception){
            System.out.println("exception.getMessage();" + exception.getMessage());
        }
    }

    public  void updateContributionRecord(ContributionRecordDao contributionRecordDao){
        ContributionRecord contributionRecord = new ContributionRecord();
        contributionRecord.setContribution_record_id
                (contributionRecordDao.getContribution_record_id() != null ?
                        Long.parseLong(encripttionService.decrypt(contributionRecordDao.getContribution_record_id()))
                        : null);
    }
}
