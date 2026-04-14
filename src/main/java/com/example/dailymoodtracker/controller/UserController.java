package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.UserDto;
import com.example.dailymoodtracker.exception.ResourceNotFoundException;
import com.example.dailymoodtracker.mapper.UserMapper;
import com.example.dailymoodtracker.model.User;
import com.example.dailymoodtracker.repository.UserRepository;
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
public class UserController {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserController(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto dto) {
        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return repository.findAllDto();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User user = repository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found: " + id));

        return mapper.toDto(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}