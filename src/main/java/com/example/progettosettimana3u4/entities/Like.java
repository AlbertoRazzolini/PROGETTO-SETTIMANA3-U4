package com.example.progettosettimana3u4.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
// Un utente non può mettere più di un like allo stesso post:
// vincolo unique sulla coppia (user_id, post_id). QUesto grazie a @UniqueConstraint.
@Table(name = "likes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Like {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}