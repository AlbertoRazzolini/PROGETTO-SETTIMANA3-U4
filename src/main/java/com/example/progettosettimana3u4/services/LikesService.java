package com.example.progettosettimana3u4.services;

import com.example.progettosettimana3u4.entities.Like;
import com.example.progettosettimana3u4.entities.Post;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.ForbiddenException;
import com.example.progettosettimana3u4.exceptions.NotFoundException;
import com.example.progettosettimana3u4.exceptions.ValidationException;
import com.example.progettosettimana3u4.repositories.LikesRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostsService postsService;

    public LikesService(LikesRepository likesRepository, PostsService postsService) {
        this.likesRepository = likesRepository;
        this.postsService = postsService;
    }

    public Like findById(UUID id) {
        return this.likesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Like con id " + id + " non trovato"));
    }

    public Like add(UUID postId, User currentUser) {
        if (this.likesRepository.existsByUserIdAndPostId(currentUser.getId(), postId)) {
            throw new ValidationException("Hai già messo like a questo post");
        }

        Post post = this.postsService.findById(postId);
        Like newLike = new Like(currentUser, post);
        return this.likesRepository.save(newLike);
    }

    public void remove(UUID likeId, User currentUser) {
        Like like = this.findById(likeId);

        if (!like.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Non puoi rimuovere un like che non è tuo");
        }

        this.likesRepository.delete(like);
    }

}