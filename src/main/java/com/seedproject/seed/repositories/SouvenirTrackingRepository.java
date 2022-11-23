package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.SouvenirTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SouvenirTrackingRepository extends JpaRepository<SouvenirTracking, Long> {
}
