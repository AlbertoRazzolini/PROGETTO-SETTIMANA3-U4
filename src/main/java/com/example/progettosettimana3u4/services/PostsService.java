package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.Post;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.NotFoundException;
import com.example.progettosettimana3u4.payloads.PostReqDTO;
import com.example.progettosettimana3u4.repositories.PostsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostsService {

    private final PostsRepository postsRepository;

    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public List<Post> getAll() {
        return this.postsRepository.findAll();
    }

    public Post findById(UUID id) {
        return this.postsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post con id " + id + " non trovato"));
    }

    public Post create(PostReqDTO payload, User author) {
        Post newPost = new Post(payload.content(), LocalDateTime.now(), author);
        return this.postsRepository.save(newPost);
    }

    public Post update(UUID postId, PostReqDTO payload) {
        Post post = this.findById(postId);
        post.setContent(payload.content());
        return this.postsRepository.save(post);
    }

}