package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.Goal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Goal> findWithUserById(Long id);
}