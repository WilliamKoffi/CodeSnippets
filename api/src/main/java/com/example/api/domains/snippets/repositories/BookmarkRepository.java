package com.example.api.domains.snippets.repositories;

import com.example.api.domains.snippets.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, String> {
    boolean existsBySnippetIdAndUserId(String snippetId, String userId);
    Optional<Bookmark> findBySnippetIdAndUserId(String snippetId, String userId);
}
