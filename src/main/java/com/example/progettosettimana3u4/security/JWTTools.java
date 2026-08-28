package com.example.progettosettimana3u4.security;

import com.example.progettosettimana3u4.entities.User;
import com.example.progettosettimana3u4.exceptions.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JWTTools {

	@Value("${jwt.secret}") // <-- Serve per leggere il secret da application.properties
	private String secret;

	public String generateToken(User user) {
		return Jwts.builder()
				.subject(String.valueOf(user.getId())) // subject, cioè a chi appartiene il token (ID DELL'UTENTE) N.B. NO DATI SENSIBILI!!!
				.issuedAt(new Date(System.currentTimeMillis())) // IssuedAt (IaT) cioè data di emissione del token, va messa in millisecondi
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Expiration, cioè data di scadenza, va messa in millisecondi
				.signWith(Keys.hmacShaKeyFor(secret.getBytes())) // Firmiamo il token (con l'algoritmo HMAC-SHA e il SECRET contenuto in application.properties) per l'integrità del token
				.compact(); // Prende tutte le info di sopra e crea il token
	}

	public void verifyToken(String token) {
		try {
			Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
			// parse() legge il token e si accorge se qualcosa non va
			// quindi lancerà un'eccezione se il token è scaduto
			// un'altra eccezione se il token è stato modificato
			// un'altra ancora se il token non è completo di tutte le sue parti
		} catch (Exception ex) { // Con questo catch catturo tutte le possibili eccezioni e le "converto" in una sola UnauthorizedException
			throw new UnauthorizedException("Token non valido rifare login!");
		}
	}

	public UUID extractIdFromToken(String token) {
		return UUID.fromString(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parseSignedClaims(token).getPayload().getSubject());
	}
}
