package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ActivityNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityNewRepository extends JpaRepository<ActivityNew, Long> {
}
