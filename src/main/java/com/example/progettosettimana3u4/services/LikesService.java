package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.Like;
import com.example.progettosettimana3u4.entities.Post;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.BadRequestException;
import com.example.progettosettimana3u4.repositories.LikesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final UsersService usersService;
    private final PostsService postsService;

    @Autowired
    public LikesService(LikesRepository likesRepository, UsersService usersService, PostsService postsService) {
        this.likesRepository = likesRepository;
        this.usersService = usersService;
        this.postsService = postsService;
    }

    public List<Like> findAll() {
        return this.likesRepository.findAll();
    }

    public Like save(UUID userId, UUID postId) {
        User user = this.usersService.findById(userId);
        Post post = this.postsService.findById(postId);

        if (this.likesRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new BadRequestException("L'utente ha già messo like a questo post");
        }

        Like newLike = new Like(user, post);
        return this.likesRepository.save(newLike);
    }
}