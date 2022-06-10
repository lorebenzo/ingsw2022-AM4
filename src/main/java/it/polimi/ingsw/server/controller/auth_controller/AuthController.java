package it.polimi.ingsw.server.controller.auth_controller;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.messages.JWTMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.LoginMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.SignUpMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.KOMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKMsg;
import it.polimi.ingsw.server.controller.games_manager.GamesManager;
import it.polimi.ingsw.server.repository.UsersRepository;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;
import it.polimi.ingsw.server.repository.interfaces.UsersRepositoryInterface;
import it.polimi.ingsw.server.server_logic.GameServer;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;


public class AuthController extends SugarMessageProcessor {
    private final UsersRepositoryInterface usersRepository = UsersRepository.getInstance();
    private static final Dotenv dotenv = Dotenv.configure().load();
    private static final String hashedKey = DigestUtils.sha256Hex(dotenv.get("JWT_KEY"));
    public final GamesManager gamesManager;
    private static final SecretKey key = Keys.hmacShaKeyFor(hashedKey.getBytes(StandardCharsets.UTF_8));

    public AuthController(GameServer gameServer) {
        this.gamesManager = new GamesManager(gameServer);
    }

    private static boolean stringNotValid(String s) {
        return s == null || s.length() <= 0;
    }

    @SugarMessageHandler
    public SugarMessage signUpMsg(SugarMessage message, Peer peer) {
        var msg = (SignUpMsg) message;

        if(stringNotValid(msg.username))
            return new KOMsg("Cannot create the user, please provide a not empty username");
        else if(stringNotValid(msg.password))
            return new KOMsg("Cannot create the user, please provide a not empty password");

        var hashedPassword = DigestUtils.sha256Hex(msg.password);

        try {
            usersRepository.signUpUser(msg.username, hashedPassword);
            return new OKMsg("Successfully signed up");
        } catch (DBQueryException e) {
            e.printStackTrace();
        }
        return new KOMsg("Cannot create the user, please check the password or the username field");
    }

    @SugarMessageHandler
    public SugarMessage loginMsg(SugarMessage message, Peer peer) {
        var msg = (LoginMsg) message;

        if(stringNotValid(msg.username))
            return new KOMsg("Cannot login, please provide a not empty username");
        else if(stringNotValid(msg.password))
            return new KOMsg("Cannot login, please provide a not empty password");

        var hashedPassword = DigestUtils.sha256Hex(msg.password);

        try {
           if(usersRepository.getUserHashedPassword(msg.username).equals(hashedPassword)) {
               var jwt = Jwts.builder()
                       .claim("username", msg.username)
                       .signWith(key)
                       .compact();

               return new JWTMsg(jwt);
           }
        } catch (DBQueryException e) {
            e.printStackTrace();
        }

        return new KOMsg("Username or password not valid");
    }

    /**
     * Returns true if the jwt is correctly configured, it is not expired
     * It doesn't check if the body of the jwt is correct
     * @param jwt of the logged user
     */
    private boolean isLoggedIn(String jwt) {
        if(jwt == null)
            return false;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        } catch (JwtException ignored) {
            return false;
        }
    }

    /**
     * Get username from the jwt claims
     * @param jwt String of the encoded jwt
     * @return the username of the user logged in, null if there is no username claims
     * @throws IllegalArgumentException when you provide a malformed JWT, an encrypted jwt with the wrong key
     */
    public static String getUsernameFromJWT(String jwt) {
        if(stringNotValid(jwt)) throw new IllegalArgumentException("Jwt must be null or a valid JWT");

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .get("username", String.class);
        } catch(Exception e) {
            throw new IllegalArgumentException("Jwt malformed");
        }
    }

    public static String createFakeJWT() {
        return Jwts.builder()
                .claim("username", "username")
                .signWith(key)
                .compact();
    }

    @SugarMessageHandler
    public SugarMessage base(SugarMessage message, Peer peer) {
        if(isLoggedIn(message.jwt)) {
            return this.gamesManager.process(message, peer);
        }
        return new KOMsg("Not logged in, please login first");
    }
}
