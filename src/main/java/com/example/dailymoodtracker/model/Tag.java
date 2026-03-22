package com.example.dailymoodtracker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<MoodEntry> moodEntries;

    public Tag() {
        //Required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<MoodEntry> getMoodEntries() {
        return moodEntries;
    }

    public void setMoodEntries(Set<MoodEntry> moodEntries) {
        this.moodEntries = moodEntries;
    }

    @Override
    public String toString() {
        return "Tag{name='" + name + "', color='" + color + "'}";
    }
}