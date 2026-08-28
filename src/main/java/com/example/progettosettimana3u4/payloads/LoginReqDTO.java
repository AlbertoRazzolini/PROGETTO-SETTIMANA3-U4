package com.example.progettosettimana3u4.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginReqDTO (

        @NotBlank(message = "La email è obbligatoria")
        @Email(message = "L'indirizzo inserito non è valido")
        String email,
        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 8, message = "La password deve essere di almeno 8 caratteri")
        String password

) {
}

/* REQUEST PAYLOAD

{
    "email": "",
    "password": ""
}

*/