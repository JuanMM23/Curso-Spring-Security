package com.app.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record AuthCreateUserRequest(@NotBlank String username, @NotBlank String password, @Valid AuthCreateRoleRequest authCreateRoleRequest) {
}
