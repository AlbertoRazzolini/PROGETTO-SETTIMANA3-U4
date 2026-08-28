package com.example.progettosettimana3u4.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name= "users")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    public User(String username, String name, String surname, String email) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

}
