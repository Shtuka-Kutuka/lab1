package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Goal DTO")
public record GoalDto(
    @Schema(example = "1")
    Long id,

    @NotNull(message = "userId is required")
    @Schema(example = "1")
    Long userId,

    @NotBlank(message = "title cannot be empty")
    @Schema(example = "Learn Spring")
    String title,

    String description,

    @NotNull(message = "targetDate required")
    @Schema(example = "2026-12-31")
    LocalDate targetDate,

    boolean achieved
) { }