package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ContributionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionConfigRepository extends JpaRepository<ContributionConfig,Long> {
}

