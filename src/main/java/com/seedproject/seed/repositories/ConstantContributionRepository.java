package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ConstantContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstantContributionRepository extends JpaRepository<ConstantContribution,Long> {
}