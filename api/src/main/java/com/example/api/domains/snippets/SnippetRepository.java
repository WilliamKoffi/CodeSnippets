package com.example.api.domains.snippets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, String> {

    @Query("SELECT DISTINCT s FROM Snippet s LEFT JOIN s.tags t " +
           "WHERE (:search IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:tag IS NULL OR LOWER(t.name) = LOWER(:tag)) " +
           "AND (:type IS NULL OR s.type = :type) " +
           "ORDER BY s.created DESC")
    List<Snippet> findAllFiltered(
        @Param("search") String search,
        @Param("tag") String tag,
        @Param("type") String type
    );
}
