package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.GoalDto;
import com.example.dailymoodtracker.dto.UserWithGoalsDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.model.Goal;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.GoalRepository;
import com.example.dailymoodtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GoalService {

    private static final String GOAL_NOT_FOUND = "Goal not found: ";
    private static final String USER_NOT_FOUND = "User not found: ";

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public Goal create(Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new DataConflictException("Title cannot be empty");
        }

        if (goal.getUser() != null) {
            Long userId = goal.getUser().getId();

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

            goal.setUser(user);
        }

        return goalRepository.save(goal);
    }

    public List<Goal> getAll() {
        return goalRepository.findAll();
    }

    public Goal getById(Long id) {
        return goalRepository.findWithUserById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GOAL_NOT_FOUND + id));
    }

    @Transactional
    public Goal update(Long id, GoalDto dto) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GOAL_NOT_FOUND + id));

        if (dto.title() == null) {
            throw new DataConflictException("Title cannot be null");
        }

        goal.setTitle(dto.title());
        goal.setDescription(dto.description());
        goal.setTargetDate(dto.targetDate());
        goal.setAchieved(dto.achieved());

        return goalRepository.save(goal);
    }

    public void delete(Long id) {
        Goal goal = goalRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(GOAL_NOT_FOUND + id));

        goalRepository.delete(goal);
    }

    private void createUserWithGoalsInternal(UserWithGoalsDto dto) {

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());

        user = userRepository.save(user);

        for (GoalDto goalDto : dto.goals()) {

            Goal goal = new Goal();
            goal.setTitle(goalDto.title());
            goal.setDescription(goalDto.description());
            goal.setTargetDate(goalDto.targetDate());
            goal.setAchieved(goalDto.achieved());
            goal.setUser(user);

            // имитируем ошибку
            if (goal.getTitle() == null) {
                throw new DataConflictException("Title cannot be null");
            }

            goalRepository.save(goal);
        }
    }

    public void createUserWithGoalsNoTransaction(UserWithGoalsDto dto) {
        createUserWithGoalsInternal(dto);
    }

    @Transactional
    public void createUserWithGoalsWithTransaction(UserWithGoalsDto dto) {
        createUserWithGoalsInternal(dto);
    }
}