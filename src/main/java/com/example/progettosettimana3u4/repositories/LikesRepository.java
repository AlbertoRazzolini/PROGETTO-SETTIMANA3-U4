package com.example.progettosettimana3u4.repositories;

import com.example.progettosettimana3u4.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikesRepository extends JpaRepository<Like, UUID> {
    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
}
