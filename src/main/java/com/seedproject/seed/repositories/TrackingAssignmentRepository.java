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
            "          ,ta.start_date, ta.end_date, ta.status, su.name, su.lastname, su.phone, su.dni,\n" +
            "            su.email,cc.contribution_config_id, cc.contribution_key, co.contributor_state ,\n" +
            "            crr.total_contribution, crr.last_payment_date\n" +
            "            from tracking_assignment ta\n" +
            "            inner join contributor co\n" +
            "            on co.contributor_id = ta.contributor_id\n" +
            "            inner join seed_user su\n" +
            "            on co.user_id = su.user_id\n" +
            "            inner join (select con_c.contribution_key, con_c.contribution_config_id, sc.contributor_id\n" +
            "\t\t\t\t\t\tfrom contribution_config con_c \n" +
            "\t\t\t\t\t\tinner join seed_configuration sc\n" +
            "\t\t\t\t\t\ton sc.contribution_config_id = con_c.contribution_config_id\n" +
            "\t\t\t\t\t\twhere con_c.is_active = true\n" +
            "\t\t\t\t\t   )cc\n" +
            "\t\t\ton cc.contributor_id = co.contributor_id\n" +
            "            left join (select cr.tracking_assignment_id,SUM (cr.contribution_ammount) as total_contribution,\n" +
            "            MAX (cr.payment_date) as last_payment_date\n" +
            "            from contribution_record cr\n where cr.register_exist = true " +
            "            group by cr.tracking_assignment_id) crr\n" +
            "            on crr.tracking_assignment_id = ta.tracking_assignment_id\n" +
            "            where ta.volunter_id=:volunter_id and ta.status = 'ACTIVE'\n" +
            "            order by su.name",nativeQuery = true)
    List<TrackingSeedDTO> findByTrackingContributors(@Param("volunter_id")Long contributor_id);
}