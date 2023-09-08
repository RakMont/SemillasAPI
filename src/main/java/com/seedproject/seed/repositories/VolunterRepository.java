package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.models.entities.Volunter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VolunterRepository extends JpaRepository<Volunter, Long> {
    @Transactional
    @Modifying
    @Query(value="update Volunter set password=:password " +
            "where volunter_id=:volunteerId",nativeQuery = true)
    void setNewPassword(@Param("volunteerId")Long id,@Param("password")String password);
    Volunter getByUsername(String username);
    @Transactional
    @Modifying
    @Query(value="update Volunter set register_exist=false " +
            "where volunter_id=:id",nativeQuery = true)
    void setRegisterFalse(@Param("id")Long id);
}
