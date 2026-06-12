package com.example.api.domains.snippets.repositories;

import com.example.api.domains.snippets.domain.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, String> {

    @Query("SELECT DISTINCT s FROM Snippet s LEFT JOIN s.tags t " +
           "WHERE (CAST(:search AS string) IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "AND (CAST(:tag AS string) IS NULL OR LOWER(t.name) = LOWER(CAST(:tag AS string))) " +
           "AND (CAST(:type AS string) IS NULL OR s.type = CAST(:type AS string)) " +
           "ORDER BY s.created DESC")
    List<Snippet> findAllFiltered(
        @Param("search") String search,
        @Param("tag") String tag,
        @Param("type") String type
    );
}
