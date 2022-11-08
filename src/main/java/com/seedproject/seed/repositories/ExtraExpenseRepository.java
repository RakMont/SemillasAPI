package com.seedproject.seed.repositories;

import com.seedproject.seed.models.entities.ExtraExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraExpenseRepository extends JpaRepository<ExtraExpense,Long> {
}