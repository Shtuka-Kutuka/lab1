package com.example.dailymoodtracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AsyncTaskStartRequestDto(
    @NotNull
    @Min(value = 1, message = "workUnits must be >= 1")
    @Max(value = 200, message = "workUnits must be <= 200")
    Integer workUnits
) { }
