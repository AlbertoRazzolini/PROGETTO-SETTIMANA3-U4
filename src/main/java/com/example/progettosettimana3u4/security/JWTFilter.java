package com.example.progettosettimana3u4.security;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.UnauthorizedException;
import com.example.progettosettimana3u4.services.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.UUID;

@Component
public class JWTFilter extends OncePerRequestFilter {

	private final JWTTools jwtTools;
	private final UsersService usersService;

	public JWTFilter(JWTTools jwtTools, UsersService usersService) {
		this.jwtTools = jwtTools;
		this.usersService = usersService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) throw new UnauthorizedException("Inserire il token nell'header");

		String accessToken = header.replace("Bearer ", "");

		jwtTools.verifyToken(accessToken); // 5. Se il token non va bene -> ERRORE

		// ************************************** AUTHORIZATION *************************************
		// Vogliamo associare la richiesta corrente all'utente vero e proprio che sta effettuando la richiesta
		// Molto utile perché quando la richiesta arriverà agli endpoint, conterrà anche le informazioni relative
		// all'utente (utili per verificare il ruolo dell'utente o per verificare se esso sia il proprietario della risorsa
		// che intende modificare)
		// Grazie a questo step potremo:
		// - controllare che una certa operazione la possano fare solo gli admin
		// - verificare che una certa operazione di modifica/cancellazione la possa fare solo l'effettivo proprietario di tale risorsa
		// - associare in fase di creazione di un Video l'effettivo proprietario di quel Video (e non usare l'id di altri!!!)

		UUID currentUserId = jwtTools.extractIdFromToken(accessToken);
		User currentUser = this.usersService.findById(currentUserId);

		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);

	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		return new AntPathMatcher().match("/api/auth/**", request.getServletPath());

	}
}
