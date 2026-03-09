package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    @EntityGraph(attributePaths = {"moodEntries", "goals"})
    List<User> findAll();
}