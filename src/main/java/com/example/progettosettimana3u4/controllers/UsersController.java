package com.example.progettosettimana3u4.controllers;


import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.payloads.AssignRoleDTO;
import com.example.progettosettimana3u4.services.UsersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/* ******************************* USERS CRUD *********************************

1. PATCH http://localhost:5174/api/users/{userId}/role (+request.body) , risponde con l'utente modificato nel payload

*/
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('MODERATOR')")
    public User changeRoleById(@PathVariable UUID userId,@Validated @RequestBody AssignRoleDTO body) {
        return this.usersService.findByIdAndUpdateRole(userId, body);
    }

}
