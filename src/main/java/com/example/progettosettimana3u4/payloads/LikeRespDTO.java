package com.example.progettosettimana3u4.payloads;

import java.util.UUID;

public record LikeRespDTO(
        UUID id,
        UUID postId,
        UUID userId
) {
}