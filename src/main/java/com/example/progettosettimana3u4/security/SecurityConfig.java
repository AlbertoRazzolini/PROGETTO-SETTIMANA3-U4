package com.example.progettosettimana3u4.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JWTFilter jwtFilter) {
		httpSecurity.formLogin(formLogin -> formLogin.disable());
		httpSecurity.csrf(csrf -> csrf.disable());
		httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		httpSecurity.authorizeHttpRequests(req -> req.requestMatchers("/**").permitAll());

		// Inseriamo il nostro filtro JWT PRIMA di quello standard di autenticazione,
		// così quando Spring Security dovrà controllare l'autorizzazione, il SecurityContext
		// sarà già stato popolato con l'utente autenticato tramite il token
		httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		httpSecurity.cors(Customizer.withDefaults());

		return httpSecurity.build();
	}

	@Bean
	public FilterRegistrationBean<JWTFilter> disableAutoRegistration(JWTFilter jwtFilter) {
		// JWTFilter è un @Component, quindi Spring Boot lo registrerebbe ANCHE come filtro
		// servlet generico (in aggiunta a quello aggiunto sopra dentro la security chain),
		// facendolo eseguire due volte per ogni richiesta. Questo bean disattiva quella
		// registrazione automatica, lasciando solo quella esplicita in securityFilterChain
		FilterRegistrationBean<JWTFilter> registrationBean = new FilterRegistrationBean<>(jwtFilter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}

	@Bean
	public PasswordEncoder getBCrypt() {

		return new BCryptPasswordEncoder(14);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
