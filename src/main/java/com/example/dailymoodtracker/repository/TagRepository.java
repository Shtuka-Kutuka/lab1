package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @EntityGraph(attributePaths = {"moodEntries"})
    List<Tag> findAll();

    @EntityGraph(attributePaths = {"moodEntries"})
    Optional<Tag> findWithMoodEntriesById(Long id);

    // ✅ ДОГРУЗКА TAGS ДЛЯ NATIVE
    @Query("""
        SELECT me.id, t
        FROM MoodEntry me
        JOIN me.tags t
        WHERE me.id IN :ids
        """)
    List<Object[]> findTagsByMoodEntryIds(List<Long> ids);
}