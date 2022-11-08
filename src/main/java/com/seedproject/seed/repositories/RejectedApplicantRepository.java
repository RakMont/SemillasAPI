package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.DeactivatedContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectedApplicantRepository extends JpaRepository<DeactivatedContributor,Long> {
}
