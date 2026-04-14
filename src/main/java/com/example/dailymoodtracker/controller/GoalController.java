package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.GoalDto;
import com.example.dailymoodtracker.dto.UserWithGoalsDto;
import com.example.dailymoodtracker.mapper.GoalMapper;
import com.example.dailymoodtracker.model.Goal;
import com.example.dailymoodtracker.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "Goals", description = "Goal management API")
public class GoalController {

    private final GoalService service;
    private final GoalMapper mapper;

    public GoalController(GoalService service, GoalMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Create goal")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto create(@Valid @RequestBody GoalDto dto) {
        Goal goal = mapper.toEntity(dto);
        return mapper.toDto(service.create(goal));
    }

    @Operation(summary = "Get all goals")
    @GetMapping
    public List<GoalDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @Operation(summary = "Get goal by id")
    @GetMapping("/{id}")
    public GoalDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @Operation(summary = "Update goal")
    @PutMapping("/{id}")
    public GoalDto update(@PathVariable Long id, @Valid @RequestBody GoalDto dto) {
        return mapper.toDto(service.update(id, dto));
    }

    @Operation(summary = "Delete goal")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Test without transaction")
    @PostMapping("/test/no-tx-related")
    public void testNoTransactionRelated(@RequestBody UserWithGoalsDto dto) {
        service.createUserWithGoalsNoTransaction(dto);
    }

    @Operation(summary = "Test with transaction")
    @PostMapping("/test/tx-related")
    public void testTransactionRelated(@RequestBody UserWithGoalsDto dto) {
        service.createUserWithGoalsWithTransaction(dto);
    }
}