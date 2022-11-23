package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.BenefitedCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitedCollaboratorRepository extends JpaRepository<BenefitedCollaborator, Long> {
}
