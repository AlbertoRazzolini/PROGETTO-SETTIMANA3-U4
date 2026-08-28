package com.example.progettosettimana3u4.payloads;

import com.example.progettosettimana3u4.enums.Role;

import java.util.UUID;

public record NewUserRespDTO(

        UUID userId,
        Role role

) {
}

/* RESP PAYLOAD
{
    "userId": id,
    "role": "MEMBER"
}
 */