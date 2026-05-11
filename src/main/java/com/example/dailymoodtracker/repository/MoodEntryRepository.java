package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.MoodEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    @Query("""
        SELECT DISTINCT m FROM MoodEntry m
        LEFT JOIN FETCH m.user
        LEFT JOIN FETCH m.moodType
        LEFT JOIN FETCH m.tags
        """)
    java.util.List<MoodEntry> findAll();


    @Query(
        value = """
            SELECT DISTINCT m FROM MoodEntry m
            JOIN m.moodType mt
            LEFT JOIN FETCH m.user
            LEFT JOIN FETCH m.moodType
            LEFT JOIN FETCH m.tags
            WHERE m.user.id = :userId AND mt.name = :moodName
        """,
        countQuery = """
            SELECT COUNT(m) FROM MoodEntry m
            JOIN m.moodType mt
            WHERE m.user.id = :userId AND mt.name = :moodName
        """
    )
    Page<MoodEntry> findComplex(Long userId, String moodName, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT 
                m.id,
                m.user_id,
                m.mood_type_id,
                m.note,
                m.entry_date,
                m.created_at
            FROM mood_entry m
            JOIN mood_type mt ON m.mood_type_id = mt.id
            WHERE m.user_id = :userId AND mt.name = :moodName
        """,
        countQuery = """
            SELECT COUNT(DISTINCT m.id)
            FROM mood_entry m
            JOIN mood_type mt ON m.mood_type_id = mt.id
            WHERE m.user_id = :userId AND mt.name = :moodName
        """,
        nativeQuery = true
    )
    Page<MoodEntry> findComplexNative(Long userId, String moodName, Pageable pageable);

    @EntityGraph(attributePaths = {"tags"})
    List<MoodEntry> findByUserId(Long userId);

}