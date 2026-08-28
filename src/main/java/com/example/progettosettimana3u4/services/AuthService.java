package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.UnauthorizedException;
import com.example.progettosettimana3u4.payloads.LoginReqDTO;
import com.example.progettosettimana3u4.repositories.UsersRepository;
import com.example.progettosettimana3u4.security.JWTTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bcrypt;
    private final JWTTools jwtTools;

    public AuthService(UsersRepository usersRepository, PasswordEncoder bcrypt, JWTTools jwtTools) {
        this.usersRepository = usersRepository;
        this.bcrypt = bcrypt;
        this.jwtTools = jwtTools;
    }

    public String checkCredentialsAndGenerateToken(LoginReqDTO body) {
        User user = this.usersRepository.findByEmail(body.email())
                .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));

        if (!this.bcrypt.matches(body.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        return this.jwtTools.generateToken(user);
    }

}