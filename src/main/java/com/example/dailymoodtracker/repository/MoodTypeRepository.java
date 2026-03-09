package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.MoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MoodTypeRepository extends JpaRepository<MoodType, Long> {
    Optional<MoodType> findByName(String name);
}