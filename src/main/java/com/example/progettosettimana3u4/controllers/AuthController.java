package com.example.progettosettimana3u4.controllers;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.payloads.LoginReqDTO;
import com.example.progettosettimana3u4.payloads.LoginRespDTO;
import com.example.progettosettimana3u4.payloads.NewUserReqDTO;
import com.example.progettosettimana3u4.payloads.NewUserRespDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/* ******************************* AUTH CRUD *********************************

1. POST http://localhost:5174/api/auth/register (+request.body), risponde 201 CREATED (nel payload l'utente creato)
2. POST http://localhost:5174/api/auth/login (+request.body)

*/

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public NewUserRespDTO createUser(@Valid @RequestBody NewUserReqDTO payload) {

        User utenteCreato = this.userService.create(payload);

        return new NewUserRespDTO(utenteCreato.getId(), utenteCreato.getRole());

    }

    @PostMapping("/login")
    public LoginRespDTO login(@Valid @RequestBody LoginReqDTO body) {

        String accessToke = this.authService.checkCredentialsAndGenerateToken(body);

        return new LoginRespDTO(accessToke);

    }

}
