package com.example.api.domains.snippets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query("SELECT t.name AS name, COUNT(s.id) AS usageCount " +
           "FROM Snippet s JOIN s.tags t " +
           "GROUP BY t.name " +
           "ORDER BY usageCount DESC")
    List<Object[]> findTagsWithUsageCount();
}
