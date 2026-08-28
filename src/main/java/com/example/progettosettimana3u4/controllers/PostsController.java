package com.example.progettosettimana3u4.controllers;

import com.example.progettosettimana3u4.entities.Post;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.enums.Role;
import com.example.progettosettimana3u4.exceptions.ForbiddenException;
import com.example.progettosettimana3u4.payloads.PostReqDTO;
import com.example.progettosettimana3u4.payloads.PostRespDTO;
import com.example.progettosettimana3u4.services.PostsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/* ******************************* POSTS CRUD *********************************

1. POST http://localhost:5174/api/posts (+request.body), risponde 201 CREATED
2. GET  http://localhost:5174/api/posts
3. GET  http://localhost:5174/api/posts/{postId}
4. PUT  http://localhost:5174/api/posts/{postId} (+request.body)

Regole di autorizzazione:
- 1: i post possono essere creati da qualsiasi utente autenticato, ma l'autore è
  sempre e solo l'utente che fa la richiesta (preso da @AuthenticationPrincipal,
  mai da un campo del payload, altrimenti si potrebbe creare un post a nome di altri)
- 2, 3: lettura libera per qualsiasi utente autenticato, nessuna regola aggiuntiva
- 4: un MODERATOR può modificare qualsiasi post; un MEMBER solo se è l'autore
  del post. Il controllo è fatto confrontando il ruolo e l'id dell'utente autenticato
  con l'autore del post recuperato dal service

*/

@RestController
@RequestMapping("/api/posts")
public class PostsController {

    private final PostsService postsService;

    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostRespDTO create(@Valid @RequestBody PostReqDTO payload, @AuthenticationPrincipal User currentUser) {
        Post created = this.postsService.create(payload, currentUser);
        return toDTO(created);
    }

    @GetMapping
    public List<PostRespDTO> getAll() {
        return this.postsService.getAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{postId}")
    public PostRespDTO findById(@PathVariable UUID postId) {
        return toDTO(this.postsService.findById(postId));
    }

    @PutMapping("/{postId}")
    public PostRespDTO update(@PathVariable UUID postId,
                               @AuthenticationPrincipal User currentUser,
                               @Valid @RequestBody PostReqDTO payload) {

        Post post = this.postsService.findById(postId);
        boolean isModerator = currentUser.getRole() == Role.MODERATOR;
        boolean isOwner = post.getUser().getId().equals(currentUser.getId());

        if (!isModerator && !isOwner) {
            throw new ForbiddenException("Non puoi modificare un post che non è tuo");
        }

        return toDTO(this.postsService.update(postId, payload));
    }

    private PostRespDTO toDTO(Post post) {
        return new PostRespDTO(
                post.getId(),
                post.getContent(),
                post.getPublicationDate(),
                post.getUser().getId(),
                post.getUser().getUsername()
        );
    }

}