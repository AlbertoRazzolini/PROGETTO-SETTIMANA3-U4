package com.example.progettosettimana3u4.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter (AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String content;

    @Column(name = "publication_date", nullable = false)
    private LocalDateTime publicationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Post(String content, LocalDateTime publicationDate, User user) {
        this.content = content;
        this.publicationDate = publicationDate;
        this.user = user;
    }
}
