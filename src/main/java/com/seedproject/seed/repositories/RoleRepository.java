package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    @Query(value="select rol.role_id as role_id, rol.role_name as role_name" +
            "            from volunter_role vr inner join " +
            "role rol on rol.role_id = vr.role_id where vr.volunter_id=:volunter_id",nativeQuery = true)
    List<Role> getVolunterRoles(@Param("volunter_id")Long id);
}
