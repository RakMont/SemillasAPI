package com.seedproject.seed.repositories;

import com.seedproject.seed.models.dto.ComboSeed;
import com.seedproject.seed.models.dto.interfaces.SeedDTO;
import com.seedproject.seed.models.entities.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface ContributorRepository extends JpaRepository<Contributor,Long> {
    @Query(value="select contr.contributor_id as contributor_id, usr.name as name, usr.lastname as lastname" +
            ",usr.name as largename,usr.email as email, usr.phone as phone, usr.dni as dni from contributor contr inner join seed_user usr on usr.user_id = contr.user_id" +
            " where contr.contributor_state = 1",nativeQuery = true)
    List<ComboSeed> findActiveSeeds();

    @Transactional
    @Modifying
    @Query(value="update Contributor set contributor_state=:state " +
            "where contributor_id=:id",nativeQuery = true)
    void updateContributorState(@Param("id")Long id, @Param("state")int state);

    @Query(value = "SELECT      co.contributor_id, concat(su.name, ' ', su.lastname) as seed_name, su.phone, su.dni,\n" +
            "            su.email,cc.contribution_config_id, cc.contribution_key, co.contributor_state,\n" +
            "\t\t\tta.tracking_assignment_id, ta.volunter_id as tr_volunteer,vol.vol_name\n" +
            "            from contributor co\n" +
            "            inner join seed_user su\n" +
            "            on co.user_id = su.user_id\n" +
            "            inner join (select con_c.contribution_key, con_c.contribution_config_id, sc.contributor_id\n" +
            "\t\t\t\t\t\tfrom contribution_config con_c \n" +
            "\t\t\t\t\t\tinner join seed_configuration sc\n" +
            "\t\t\t\t\t\ton sc.contribution_config_id = con_c.contribution_config_id\n" +
            "\t\t\t\t\t\twhere con_c.is_active = true\n" +
            "\t\t\t\t\t   )cc\n" +
            "\t\t\ton cc.contributor_id = co.contributor_id\n" +
            "            left join (SELECT ta.contributor_id, ta.tracking_assignment_id, ta.volunter_id\n" +
            "\t\t\t\t\t  FROM tracking_assignment ta\n" +
            "\t\t\t\t\t  where ta.status='ACTIVE'\n" +
            "\t\t\t\t\t  ) ta\n" +
            "\t\t\ton ta.contributor_id = co.contributor_id\n" +
            "\t\t\tleft join (select vol.volunter_id, concat(su.name, ' ', su.lastname) as vol_name\n" +
            "\t\t\t\t\t  from volunter vol\n" +
            "\t\t\t\t\t   inner join seed_user su\n" +
            "\t\t\t\t\t   on vol.user_id = su.user_id\n" +
            "\t\t\t\t\t  ) vol\n" +
            "\t\t\ton vol.volunter_id = ta.volunter_id\n" +
            "            where co.register_exist=true and co.contributor_state=:status" +
            "            order by su.name", nativeQuery = true)
    List<SeedDTO> getSeedsAsc(@Param("status") int status);


    @Query(value = "select co.contributor_id \n" +
            "from contributor co\n" +
            "where co.contributor_state=3 and co.register_exist = true", nativeQuery = true)
    List<Long> getPendingSeeds();
}
