package com.example.progettosettimana3u4.payloads;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostRespDTO(
        UUID id,
        String content,
        LocalDateTime publicationDate,
        UUID authorId,
        String authorUsername
) {
}