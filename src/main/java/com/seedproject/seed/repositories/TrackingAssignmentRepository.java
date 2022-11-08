package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.TrackingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingAssignmentRepository extends JpaRepository<TrackingAssignment, Long> {
    @Query(value="select ta.tracking_assignment_id, ta.volunter_id, ta.contributor_id" +
            ",ta.start_date, ta.end_date from tracking_assignment ta where ta.volunter_id=:volunter_id",nativeQuery = true)
    List<TrackingAssignment> findByVolunterId(@Param("volunter_id")Long volunter_id);
}