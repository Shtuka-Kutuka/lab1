package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.UserDto;
import com.example.dailymoodtracker.exception.DataConflictException;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.mapper.UserMapper;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User API")
public class UserController {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserController(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Operation(summary = "Create user")
    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto dto) {

        if (dto.username() == null || dto.username().isBlank()) {
            throw new DataConflictException("Username cannot be empty");
        }

        if (dto.email() == null || dto.email().isBlank()) {
            throw new DataConflictException("Email cannot be empty");
        }

        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDto> getAll() {
        return repository.findAllDto();
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User user = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return mapper.toDto(user);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }

        repository.deleteById(id);
    }
}