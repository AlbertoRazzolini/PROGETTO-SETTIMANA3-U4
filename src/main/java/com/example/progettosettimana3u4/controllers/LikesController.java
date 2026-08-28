package com.example.progettosettimana3u4.controllers;

import com.example.progettosettimana3u4.entities.Like;
import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.payloads.LikeRespDTO;
import com.example.progettosettimana3u4.services.LikesService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/* ******************************* LIKES CRUD *********************************

1. POST   http://localhost:5174/api/likes/{postId}, risponde 201 CREATED
2. DELETE http://localhost:5174/api/likes/{likeId}, risponde 204 NO CONTENT

Regole di autorizzazione:
- 1: qualsiasi utente autenticato può mettere un like, ma sempre e solo a proprio
  nome: l'utente che mette il like è quello autenticato (@AuthenticationPrincipal),
  mai un id passato dal client. Un MODERATOR non ha nessun privilegio in più qui:
  può mettere like solo per sé stesso, come un MEMBER
- 2: solo l'utente che ha messo quel like può rimuoverlo. A differenza di Posts,
  qui il MODERATOR NON ha alcun potere speciale: il controllo confronta l'id
  dell'utente autenticato con l'id di chi ha messo il like, senza nessuna
  eccezione di ruolo. Stesso approccio in Java semplice (if + eccezione custom)
  usato in PostsController per l'update

*/

@RestController
@RequestMapping("/api/likes")
public class LikesController {

    private final LikesService likesService;

    public LikesController(LikesService likesService) {
        this.likesService = likesService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}")
    public LikeRespDTO add(@PathVariable UUID postId, @AuthenticationPrincipal User currentUser) {
        Like like = this.likesService.add(postId, currentUser);
        return toDTO(like);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{likeId}")
    public void remove(@PathVariable UUID likeId, @AuthenticationPrincipal User currentUser) {
        this.likesService.remove(likeId, currentUser);
    }

    private LikeRespDTO toDTO(Like like) {
        return new LikeRespDTO(like.getId(), like.getPost().getId(), like.getUser().getId());
    }

}