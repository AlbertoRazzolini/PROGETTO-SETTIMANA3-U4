package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.Post;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.BadRequestException;
import com.example.progettosettimana3u4.exceptions.NotFoundException;
import com.example.progettosettimana3u4.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final UsersService usersService;

    @Autowired
    public PostsService(PostsRepository postsRepository, UsersService usersService) {
        this.postsRepository = postsRepository;
        this.usersService = usersService;
    }

    public List<Post> findAll() { return this.postsRepository.findAll(); }

    public Post findById(UUID id) {
        return this.postsRepository.findById(id).orElseThrow(() -> new NotFoundException("Post con id " + id + " non trovato"));
    }

    public Post save(String content, LocalDateTime publicationDate, UUID userId) {

        if (content == null || content.isBlank()) {
            throw new BadRequestException("Il testo non può essere vuoto");
        }
        if (publicationDate == null) {
            throw new BadRequestException("La data deve essere inserita");
        }

        User user = this.usersService.findById(userId);
        Post newPost = new Post(content, publicationDate, user);
        return this.postsRepository.save(newPost);
    }
}
