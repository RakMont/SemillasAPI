package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.CommentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRecordRepository extends JpaRepository<CommentRecord,Long> {

}
