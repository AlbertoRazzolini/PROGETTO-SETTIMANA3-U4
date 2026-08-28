package com.example.progettosettimana3u4.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
@EnableMethodSecurity // <-- Senza quest'annotazione le varie regole PreAuthorize sugli endpoint non funzioneranno
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
		// Questo disabilita il form di login che c'è di default
		httpSecurity.formLogin(formLogin -> formLogin.disable());
		// Questo disabilita le protezioni verso CSRF che quando usiamo l'autenticazione basata su token JWT sono inutili.
		// Anzi addirittura ci complicherebbero anche il FE
		httpSecurity.csrf(csrf -> csrf.disable());
		// Disabilitiamo le sessioni. Per definizione JWT è un meccanismo SENZA SESSIONI (Stateless) quindi dobbiamo disabilitarle
		httpSecurity.sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		// Siccome di default spring security mi torna 401 su TUTTI GLI ENDPOINT, tolgo questo controllo (che verrà rimpiazzato dal mio filtro custom)
		httpSecurity.authorizeHttpRequests(req -> req.requestMatchers("/**").permitAll());

		// Se voglio usare la configurazione CORS sottostante devo aggiungere la seguente riga
		httpSecurity.cors(Customizer.withDefaults());

		return httpSecurity.build();
	}

	@Bean
	public PasswordEncoder getBCrypt() {
		// Più è alto il valore della strength/rounds più sicure saranno le password
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000", "https://www.mywonderfulfrontend.com")); // N.B. L'indirizzo del FE NON deve avere la slash finale
		// Ho impostato una WHITELIST di indirizzi FRONTEND che voglio possano comunicare con questo backend
		// Potrei anche usare '*' ma toglierebbe del tutto la protezione dei browser (utile sono nel caso di API pubbliche)

		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // <-- Imposta la configurazione fatta su /** ovvero su tutti gli endpoint del server
		return source;
	}

}
