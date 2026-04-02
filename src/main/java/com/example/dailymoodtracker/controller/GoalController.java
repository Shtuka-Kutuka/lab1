package com.example.dailymoodtracker.controller;

import com.example.dailymoodtracker.dto.GoalDto;
import com.example.dailymoodtracker.mapper.GoalMapper;
import com.example.dailymoodtracker.model.Goal;
import com.example.dailymoodtracker.service.GoalService;
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
public class GoalController {

    private final GoalService service;
    private final GoalMapper mapper;

    public GoalController(GoalService service, GoalMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto create(@RequestBody GoalDto dto) {
        Goal goal = mapper.toEntity(dto);
        return mapper.toDto(service.create(goal));
    }

    @GetMapping
    public List<GoalDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public GoalDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @PutMapping("/{id}")
    public GoalDto update(@PathVariable Long id, @RequestBody GoalDto dto) {
        return mapper.toDto(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/test/no-tx-related")
    public void testNoTransactionRelated() {
        service.createUserWithGoalsNoTransaction();
    }

    @PostMapping("/test/tx-related")
    public void testTransactionRelated() {
        service.createUserWithGoalsWithTransaction();
    }
}