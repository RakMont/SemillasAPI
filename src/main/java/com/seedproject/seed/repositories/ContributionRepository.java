package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributionRepository  extends JpaRepository<Contribution,Long> {
}
