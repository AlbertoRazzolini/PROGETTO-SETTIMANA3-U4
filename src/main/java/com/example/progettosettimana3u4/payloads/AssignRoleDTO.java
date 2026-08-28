package com.example.progettosettimana3u4.payloads;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleDTO(
        @NotBlank(message = "Il ruolo è obbligatorio")
        String role
) {
}
