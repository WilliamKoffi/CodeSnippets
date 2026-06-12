package com.example.api.domains.snippets.repositories;

import com.example.api.domains.snippets.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    boolean existsBySnippetIdAndUserId(String snippetId, String userId);
    Optional<Like> findBySnippetIdAndUserId(String snippetId, String userId);
}
