package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.enums.Role;
import com.example.progettosettimana3u4.exceptions.NotFoundException;
import com.example.progettosettimana3u4.exceptions.ValidationException;
import com.example.progettosettimana3u4.payloads.AssignRoleDTO;
import com.example.progettosettimana3u4.payloads.NewUserReqDTO;
import com.example.progettosettimana3u4.repositories.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bcrypt;

    public UsersService(UsersRepository usersRepository, PasswordEncoder bcrypt) {
        this.usersRepository = usersRepository;
        this.bcrypt = bcrypt;
    }

    public User findById(UUID id) {
        return this.usersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
    }

    public User create(NewUserReqDTO payload) {
        if (this.usersRepository.findByEmail(payload.email()).isPresent())
         throw new ValidationException("L'email " + payload.email() + " è già in uso");

        if (this.usersRepository.findByUsername(payload.username()).isPresent())
         throw new ValidationException("Lo username " + payload.username() + " è già in uso");

        User newUser = new User(payload.username(), payload.name(), payload.surname(), payload.email(), bcrypt.encode(payload.password()));
        return this.usersRepository.save(newUser);
    }

    public User findByIdAndUpdateRole(UUID userId, AssignRoleDTO body) {
        User user = this.findById(userId);

        Role newRole;
        try {                                                  //Così faccio in modo tale che gli unici valori possano
            newRole = Role.valueOf(body.role().toUpperCase()); //essere solo MEMBER o MODERATOR
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Ruolo non valido, i valori ammessi sono MEMBER o MODERATOR");
        }

        user.setRole(newRole);
        return this.usersRepository.save(user);
    }

}