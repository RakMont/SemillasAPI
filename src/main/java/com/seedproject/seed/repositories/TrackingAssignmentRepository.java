package com.seedproject.seed.repositories;

import com.seedproject.seed.models.dto.TrackingSeedDTO;
import com.seedproject.seed.models.entities.TrackingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingAssignmentRepository extends JpaRepository<TrackingAssignment, Long> {
    @Query(value="select ta.tracking_assignment_id, ta.volunter_id, ta.contributor_id" +
            ",ta.start_date, ta.end_date, ta.status from tracking_assignment ta where ta.volunter_id=:volunter_id",nativeQuery = true)
    List<TrackingAssignment> findByVolunterId(@Param("volunter_id")Long volunter_id);

    @Query(value="select ta.tracking_assignment_id, ta.volunter_id, ta.contributor_id" +
            ",ta.start_date, ta.end_date, ta.status from tracking_assignment ta " +
            "where ta.contributor_id=:contributor_id and ta.status = 'ACTIVE' ",nativeQuery = true)
    List<TrackingAssignment> findByContributorId(@Param("contributor_id")Long contributor_id);

    @Query(value="SELECT ta.tracking_assignment_id, ta.volunter_id, ta.contributor_id\n" +
            "            ,ta.start_date, ta.end_date, ta.status, su.name, su.lastname, su.phone, su.dni," +
            "\t\t\tsu.email,cc.contribution_config_id, cc.contribution_key, co.contributor_state ," +
            "crr.total_contribution, crr.last_payment_date" +
            "\t\t\tfrom \n" +
            "\t\t\ttracking_assignment ta \n" +
            "\t\t\tinner join contributor co \n" +
            "\t\t\ton co.contributor_id = ta.contributor_id \n" +
            "\t\t\tinner join seed_user su\n" +
            "\t\t\ton co.user_id = su.user_id\n" +
            "\t\t\tinner join contribution_config cc\n" +
            "\t\t\ton cc.contribution_config_id = co.contribution_config_id\n" +
            "left join (\n" +
            "\t\t\t\tselect cr.tracking_assignment_id,\n" +
            "\t\t\t\tSUM (cr.contribution_ammount) as total_contribution,\n" +
            "\t\t\t\tMAX (cr.payment_date) as last_payment_date\n" +
            "\t\t\t\tfrom contribution_record cr\n" +
            "\t\t\t\tgroup by cr.tracking_assignment_id) crr\n" +
            "\t\t\ton crr.tracking_assignment_id = ta.tracking_assignment_id" +
            "            where ta.volunter_id=:volunter_id and ta.status = 'ACTIVE'" +
            "order by su.name",nativeQuery = true)
    List<TrackingSeedDTO> findByTrackingContributors(@Param("volunter_id")Long contributor_id);
}