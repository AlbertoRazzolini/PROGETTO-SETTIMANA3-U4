package com.example.progettosettimana3u4.payloads;

import jakarta.validation.constraints.NotBlank;

public record PostReqDTO(
        @NotBlank(message = "Il contenuto non può essere vuoto")
        String content
) {
}