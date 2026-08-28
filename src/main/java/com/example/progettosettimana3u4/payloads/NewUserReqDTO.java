package com.example.progettosettimana3u4.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserReqDTO(

        @NotBlank(message = "Lo username è obbligatorio")
        @Size(min = 5, max = 15, message = "Lo username deve avere almeno 5 caratteri e massimo 15")
        String username,
        @NotBlank(message = "Il nome è obbligatorio")
        String name,
        @NotBlank(message = "Il cognome è obbligatorio")
        String surname,
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
    "username": "",
    "name": "",
    "surname": "",
    "email": "",
    "password": ""
}

*/