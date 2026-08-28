
# PROGETTO-SETTIMANA3-U4

API REST per la gestione di Users, Posts e Likes, con autenticazione basata su JWT e regole di autorizzazione differenziate in base all'operazione e al tipo di risorsa.

## Autenticazione

L'autenticazione è basata su **JWT** (JSON Web Token), stateless: non ci sono sessioni lato server, ogni richiesta protetta deve portare il token nell'header `Authorization: Bearer <token>`.

Il flusso è il seguente:

- **Registrazione** (`POST /api/auth/register`): chiunque può registrarsi fornendo username, nome, cognome, email e password. La password non viene mai salvata in chiaro, ma cifrata con **BCrypt** prima di essere scritta nel database. Ogni nuovo utente riceve automaticamente il ruolo `MEMBER`: non è possibile scegliere il proprio ruolo in fase di registrazione, altrimenti chiunque potrebbe auto-promuoversi a `MODERATOR`, vanificando la logica di autorizzazione descritta più sotto.
- **Login** (`POST /api/auth/login`): l'utente fornisce email e password; se le credenziali sono corrette, il server genera e restituisce un JWT contenente l'id dell'utente, firmato con una chiave segreta (HMAC-SHA). In caso di email inesistente o password errata, viene restituito lo **stesso identico messaggio d'errore generico** ("Credenziali non valide") con status 401, in modo da non rivelare a un utente malintenzionato se una determinata email è registrata o meno.
- **Verifica del token**: un filtro (`JWTFilter`), eseguito prima di ogni richiesta (tranne quelle su `/api/auth/**`, che devono restare pubbliche), legge il token dall'header, ne verifica firma e scadenza, recupera l'utente corrispondente dal database e lo associa alla richiesta corrente tramite il `SecurityContext` di Spring Security. Da quel momento in poi, ogni controller può sapere "chi" sta facendo la richiesta tramite `@AuthenticationPrincipal`.

Le richieste senza token valido su una rotta protetta vengono rifiutate con **401 Unauthorized**.

## Autorizzazione

L'autorizzazione, cioè "chi può fare cosa", è stata pensata operazione per operazione, perché non tutte le risorse hanno lo stesso livello di sensibilità né lo stesso "proprietario" naturale.

### Users

- **Cambio ruolo** (`PATCH /api/users/{userId}/role`) è l'unica operazione protetta su questa risorsa (registrazione e login sono pubbliche per definizione, essendo il punto di ingresso al sistema). Cambiare il ruolo di un utente (tra `MEMBER` e `MODERATOR`) è un'operazione delicata, perché incide direttamente sui permessi che quell'utente avrà in tutto il resto del sistema: per questo è stata scelta una **regola basata sul ruolo** (role-based authorization), non sulla proprietà della risorsa. Solo chi ha già il ruolo `MODERATOR` può eseguire questa operazione, su qualunque utente, incluso potenzialmente sé stesso. Non ha senso lasciare che un utente cambi il proprio ruolo o quello di altri MEMBER autonomamente: sarebbe equivalente a lasciare che chiunque si assegni i permessi che vuole.

### Posts

- **Creazione** (`POST /api/posts`): può crearla qualsiasi utente autenticato, senza distinzione di ruolo. L'unica cosa garantita è che l'autore del post è **sempre e solo** l'utente che ha effettuato la richiesta (dedotto dal token, mai da un campo del body): questo evita che qualcuno crei un post a nome di un altro utente semplicemente indicando un id diverso nel payload.
- **Lettura di tutti i post** e **lettura di un singolo post per id**: richiedono solo di essere autenticati, senza nessuna regola aggiuntiva. Non c'è motivo di nascondere il contenuto dei post a un utente autenticato solo perché non ne è l'autore: sono contenuti pensati per essere condivisi e letti da tutti gli utenti della piattaforma.
- **Aggiornamento di un post esistente** (`PUT /api/posts/{postId}`): qui la regola è **mista**, sia basata sul ruolo che sulla proprietà della risorsa (ownership-based). Un `MODERATOR` può modificare qualunque post (ha un ruolo di supervisione sui contenuti), mentre un `MEMBER` può modificare **solo i post di cui è autore**. Il controllo confronta l'id dell'utente autenticato con l'id dell'autore del post recuperato dal database; se l'utente non è né l'autore né un `MODERATOR`, la richiesta viene rifiutata con **403 Forbidden**.

### Likes

I like seguono una logica volutamente più restrittiva rispetto ai post: qui il ruolo `MODERATOR` **non ha alcun potere speciale**, perché mettere o togliere un like è un'azione personale ed espressiva (rappresenta il gradimento di un singolo utente), non un contenuto editoriale da moderare.

- **Aggiungere un like a un post**: qualunque utente autenticato può mettere un like, ma **esclusivamente a proprio nome**. Non è possibile mettere un like "per conto" di un altro utente, allo stesso modo in cui non è possibile creare un post a nome di qualcun altro: l'utente che mette il like è sempre e solo quello autenticato (dedotto dal token), mai un id passato dal client. Un `MODERATOR` non ha nessun privilegio aggiuntivo qui: può mettere like solo per sé stesso, esattamente come un `MEMBER`.
- **Rimuovere un like**: può farlo **solo ed esclusivamente** l'utente che ha messo quel like. Nemmeno un `MODERATOR` può rimuovere un like altrui: si tratta di una scelta personale dell'utente che ha interagito con il post, e nessun altro ruolo ha titolo per annullarla. Se l'utente autenticato prova a rimuovere un like che non è il suo, la richiesta viene rifiutata con **403 Forbidden**.

## Gestione degli errori

Tutte le eccezioni applicative vengono intercettate centralmente da un `@RestControllerAdvice` (`ExceptionHandler`), che le traduce in risposte JSON coerenti (`{ "message": ..., "timestamp": ... }`) con lo status HTTP corretto:

| Eccezione | Status | Significato |
|---|---|---|
| `ValidationException` | 400 Bad Request | Dati non validi o risorsa già esistente (es. email/username duplicati) |
| `UnauthorizedException` | 401 Unauthorized | Utente non autenticato o credenziali errate |
| `NotFoundException` | 404 Not Found | Risorsa richiesta inesistente |
| `ForbiddenException` | 403 Forbidden | Utente autenticato ma privo dei permessi necessari per quella specifica operazione |
| `AuthorizationDeniedException` | 403 Forbidden | Accesso negato da una regola di autorizzazione a livello di ruolo (es. cambio ruolo tentato da un MEMBER) |