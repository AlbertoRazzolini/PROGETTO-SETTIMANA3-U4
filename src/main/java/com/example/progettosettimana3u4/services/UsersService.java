package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.BadRequestException;
import com.example.progettosettimana3u4.exceptions.NotFoundException;
import com.example.progettosettimana3u4.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> findAll() {
        return this.usersRepository.findAll();
    }

    public User findById(UUID id) {
        return this.usersRepository.findById(id).orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
    }

    public User save(String username, String name, String surname, String email) {
        if (this.usersRepository.existsByUsername(username)) {
            throw new BadRequestException("Lo username '" + username + "' è già in uso");
        }
        if (this.usersRepository.existsByEmail(email)) {
            throw new BadRequestException("L'email " + email + " è già in uso");
        }

        User newUser = new User(username, name, surname, email);
        return this.usersRepository.save(newUser);
    }
}
