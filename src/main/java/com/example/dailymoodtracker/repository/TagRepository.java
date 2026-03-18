package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}