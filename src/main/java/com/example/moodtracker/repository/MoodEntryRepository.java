package com.example.moodtracker.repository;
import com.example.moodtracker.model.MoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByDate(LocalDate date);
}