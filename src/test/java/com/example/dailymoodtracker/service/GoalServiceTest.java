package com.example.dailymoodtracker.service;

import com.example.dailymoodtracker.dto.GoalDto;
import com.example.dailymoodtracker.dto.UserWithGoalsDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.model.Goal;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.GoalRepository;
import com.example.dailymoodtracker.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
        verify(goalRepository).save(goal);
    }

    @Test
    void create_titleEmpty_shouldThrow() {
        Goal goal = new Goal();
        goal.setTitle("");

        assertThrows(DataConflictException.class, () -> service.create(goal));
    }

    @Test
    void create_userNotFound_shouldThrow() {
        Goal goal = new Goal();
        goal.setTitle("Test");

        User user = new User();
        user.setId(1L);
        goal.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(goal));
    }

    @Test
    void create_forbiddenTitle_shouldThrow() {
        Goal goal = new Goal();
        goal.setTitle("Forbidden");

        assertThrows(DataConflictException.class, () -> service.create(goal));
    }

    @Test
    void getById_success() {
        Goal goal = new Goal();
        when(goalRepository.findWithUserById(1L)).thenReturn(Optional.of(goal));

        assertNotNull(service.getById(1L));
    }

    @Test
    void getById_notFound() {
        when(goalRepository.findWithUserById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void update_success() {
        Goal goal = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(goal);

        GoalDto dto = new GoalDto(null, 1L, "Title", "desc", LocalDate.now(), false);

        Goal updated = service.update(1L, dto);

        assertEquals("Title", updated.getTitle());
    }

    @Test
    void update_nullTitle_shouldThrow() {
        Goal goal = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        GoalDto dto = new GoalDto(null, 1L, null, null, LocalDate.now(), false);

        assertThrows(DataConflictException.class, () -> service.update(1L, dto));
    }

    @Test
    void delete_success() {
        Goal goal = new Goal();
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));

        service.delete(1L);

        verify(goalRepository).delete(goal);
    }

    @Test
    void delete_notFound() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void createUserWithGoals_noTransaction_partialSave() {
        UserWithGoalsDto dto = new UserWithGoalsDto(
            "user",
            "email",
            List.of(
                new GoalDto(null, null, "ok", null, LocalDate.now(), false),
                new GoalDto(null, null, null, null, LocalDate.now(), false) // ошибка
            )
        );

        when(userRepository.save(any())).thenReturn(new User());

        assertThrows(DataConflictException.class,
            () -> service.createUserWithGoalsNoTransaction(dto));
    }
}