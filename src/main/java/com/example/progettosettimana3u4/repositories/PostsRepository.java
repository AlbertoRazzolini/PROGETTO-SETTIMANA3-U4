package com.example.progettosettimana3u4.repositories;

import com.example.progettosettimana3u4.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostsRepository extends JpaRepository<Post, UUID> {
}
