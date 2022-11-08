package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ExitMessage;
import com.seedproject.seed.models.entities.Volunter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExitMessageRepository extends JpaRepository<ExitMessage,Long> {
    List<ExitMessage> findByVolunter(Volunter volunter);
}