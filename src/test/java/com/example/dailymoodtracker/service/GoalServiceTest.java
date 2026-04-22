package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.GoalDto;
import com.example.dailymoodtracker.dto.UserWithGoalsDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.Goal;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.GoalRepository;
import com.example.dailymoodtracker.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService service;

    @Test
    void create_success() {
        Goal goal = new Goal();
        goal.setTitle("Test");

        User user = new User();
        user.setId(1L);
        goal.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(goalRepository.save(goal)).thenReturn(goal);

        Goal result = service.create(goal);

        assertNotNull(result);
    }

    @Test
    void create_emptyTitle() {
        Goal goal = new Goal();

        assertThrows(DataConflictException.class, () -> service.create(goal));
    }

    @Test
    void create_userNotFound() {
        Goal goal = new Goal();
        goal.setTitle("Test");

        User user = new User();
        user.setId(99L);
        goal.setUser(user);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(goal));
    }

    @Test
    void create_forbiddenTitle() {
        Goal goal = new Goal();
        goal.setTitle("Forbidden");

        assertThrows(DataConflictException.class, () -> service.create(goal));
    }

    @Test
    void getById_notFound() {
        when(goalRepository.findWithUserById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void update_success() {
        Goal goal = new Goal();
        goal.setTitle("Old");

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GoalDto dto = new GoalDto(1L, 1L, "New", "desc", LocalDate.now(), true);

        Goal updated = service.update(1L, dto);

        assertEquals("New", updated.getTitle());
    }

    @Test
    void update_nullTitle() {
        Goal goal = new Goal();

        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        GoalDto dto = new GoalDto(1L, 1L, null, null, LocalDate.now(), false);

        assertThrows(DataConflictException.class, () -> service.update(1L, dto));
    }

    @Test
    void delete_notFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void createUserWithGoals_noTransaction_errorMidway() {
        UserWithGoalsDto dto = new UserWithGoalsDto(
            "user",
            "email",
            List.of(new GoalDto(null, null, null, null, LocalDate.now(), false))
        );

        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        assertThrows(DataConflictException.class,
            () -> service.createUserWithGoalsNoTransaction(dto));
    }
}