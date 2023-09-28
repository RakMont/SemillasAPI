package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.EnterpriseContribution;
import com.seedproject.seed.models.entities.UniqueContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterpriseContributionRepository extends JpaRepository<EnterpriseContribution,Long> {
}
