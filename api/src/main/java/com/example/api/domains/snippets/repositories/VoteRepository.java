package com.example.api.domains.snippets.repositories;

import com.example.api.domains.snippets.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, String> {
    Optional<Vote> findBySolutionIdAndUserId(String solutionId, String userId);
    List<Vote> findByUserId(String userId);
}
