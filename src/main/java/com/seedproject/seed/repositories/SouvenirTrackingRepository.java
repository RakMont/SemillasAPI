package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.SeedSouvenirTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SouvenirTrackingRepository extends JpaRepository<SeedSouvenirTracking, Long> {
}
