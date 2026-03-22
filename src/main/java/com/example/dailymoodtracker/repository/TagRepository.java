package com.example.dailymoodtracker.repository;

import com.example.dailymoodtracker.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t")
    List<Tag> findAllWithNPlusOne();
}