package com.example.progettosettimana3u4.entities;

import com.example.progettosettimana3u4.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name= "users")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class User implements UserDetails {

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

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User(String username, String name, String surname, String email, String password) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.role = Role.MEMBER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Questo metodo mi impone di ritornare un Collection di Classi che implementano l'interfaccia GrantedAuthority
        // Questo passaggio è d'obbligo perché non c'è uno standard sui ruoli dell'utente, noi abbiamo un enum ma in altre
        // applicazione potrebbe essere rappresentato da qualcos'altro
        // SimpleGrantedAuthority è la classe che rappresenta i ruoli in un modo conosciuto da Spring Security
        // Spring Security si aspetta di avere a che fare con SimpleGrantedAuthority quando dovrà fare i controlli sul ruolo
        // Questa è una lista perché in applicazioni più complesse gli utenti potrebbero avere anche più di un ruolo a testa
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
