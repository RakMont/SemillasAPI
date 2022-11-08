package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ProcessedContributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedContributorRepository extends JpaRepository<ProcessedContributor,Long> {
}