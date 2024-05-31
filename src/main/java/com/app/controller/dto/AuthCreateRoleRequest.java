package com.app.controller.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record AuthCreateRoleRequest(@Size(max = 3, message = "The user cannot have more than three roles") List<String> roleListName) {
}
