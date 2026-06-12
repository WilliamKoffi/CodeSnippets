package com.example.api.domains.snippets.repositories;

import com.example.api.domains.snippets.domain.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, String> {
}
