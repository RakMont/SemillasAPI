package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.TrackingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionRecordRepository extends JpaRepository<ContributionRecord,Long> {
    List<ContributionRecord> findByContributor( Contributor contributor);
    List<ContributionRecord> findByTrackingAssignment(TrackingAssignment trackingAssignment);

}
