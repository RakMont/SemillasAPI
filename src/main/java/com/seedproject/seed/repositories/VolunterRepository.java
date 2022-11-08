package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.Volunter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunterRepository extends JpaRepository<Volunter, Long> {
    //Optional<Volunter> findByPerson(User user);
    Volunter getByUsername(String username);
}
