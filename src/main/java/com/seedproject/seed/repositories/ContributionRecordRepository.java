package com.seedproject.seed.repositories;

import com.seedproject.seed.models.dto.interfaces.ContributionReportDTO;
import com.seedproject.seed.models.entities.ContributionRecord;
import com.seedproject.seed.models.entities.Contributor;
import com.seedproject.seed.models.entities.TrackingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface ContributionRecordRepository extends JpaRepository<ContributionRecord,Long> {
    List<ContributionRecord> findByContributor( Contributor contributor);
    List<ContributionRecord> findByTrackingAssignment(TrackingAssignment trackingAssignment);

    @Query(value = "select cr.contribution_record_id ,cr.payment_method,concat(su.name, ' ', su.lastname) as seed_name, cconf.contribution_key, \n" +
            "            cr.payment_date,cr.contribution_month, cr.expected_payment_date, cr.contribution_ammount as payment_amount,\n" +
            "            cr.extra_income_ammount as extra_amount, exex.extra_expense_amount as spent_amount,\n" +
            "            cr.receipt_code, cr.valid_transaction\n" +
            "            from contribution_record cr\n" +
            "            inner join contributor co\n" +
            "            on co.contributor_id = cr.contributor_id\n" +
            "            inner join seed_user su\n" +
            "            on co.user_id = su.user_id\n" +
            "            inner join contribution_config cconf\n" +
            "            on cconf.contribution_config_id = cr.contribution_config_id\n" +
            "            left join extra_expense exex\n" +
            "            on exex.extra_expense_id = cr.extra_expense_id\n" +
            "where cr.contributor_id=:seedId and cr.register_exist=true " +
            "order by cr.payment_date", nativeQuery = true)
    List<ContributionReportDTO> findContributionsBySeed(@Param("seedId")Long seedId);

    @Query(value="select cr.contribution_record_id ,cr.register_volunter_id as volunteer_id,\n" +
            "vol_us.reg_volunteer_name, concat(su.name, ' ', su.lastname)\n" +
            "as seed_name,cconf.contribution_key,\n" +
            "cr.payment_date,cr.contribution_month, cr.expected_payment_date,\n" +
            "cr.payment_method, cr.contribution_ammount as payment_amount,\n" +
            "cr.extra_income_ammount as extra_amount, exex.extra_expense_amount as spent_amount,\n" +
            "cr.receipt_code, cr.valid_transaction\n" +
            "from contribution_record cr\n" +
            "inner join contributor co\n" +
            "on co.contributor_id = cr.contributor_id\n" +
            "inner join seed_user su\n" +
            "on co.user_id = su.user_id\n" +
            "inner join contribution_config cconf\n" +
            "on cconf.contribution_config_id = cr.contribution_config_id\n" +
            "inner join (select vol.volunter_id,  concat(us.name, ' ', us.lastname)\n" +
            "as reg_volunteer_name\n" +
            "from volunter vol inner join seed_user us on vol.user_id = us.user_id) vol_us\n" +
            "on vol_us.volunter_id = cr.register_volunter_id\n" +
            "left join extra_expense exex\n" +
            "on exex.extra_expense_id = cr.extra_expense_id" +
            "\t\t\twhere cr.payment_date between :beginDate and :endDate and cr.register_exist=true " +
            "\t\t\torder by cr.payment_date",nativeQuery = true)
    List<ContributionReportDTO> findContributionRecord(@Param("beginDate") Date beginDate, @Param("endDate")Date endDate);

    @Transactional
    @Modifying
    @Query(value="update contribution_record set register_exist=false " +
            "where contribution_record_id=:id",nativeQuery = true)
    void deleteContributionRecord(@Param("id")Long id);

}
