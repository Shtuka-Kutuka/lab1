package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.GoalDto;
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
        return goalRepository.findById(id)
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
    private void createUserWithGoalsInternal(String username, String email) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        user = userRepository.save(user);

        Goal goal1 = new Goal();
        goal1.setTitle("Goal 1");
        goal1.setDescription("First goal");
        goal1.setUser(user);

        goalRepository.save(goal1);

        Goal goal2 = new Goal();
        goal2.setTitle(null);
        goal2.setDescription("Second goal");
        goal2.setUser(user);

        if (goal2.getTitle() == null) {
            throw new DataConflictException("Title cannot be null");
        }

        goalRepository.save(goal2);
    }
    public void createUserWithGoalsNoTransaction() {
        createUserWithGoalsInternal("test_user_no_tx", "no_tx@mail.com");
    }

    @Transactional
    public void createUserWithGoalsWithTransaction() {
        createUserWithGoalsInternal("test_user_tx", "tx@mail.com");
    }
}