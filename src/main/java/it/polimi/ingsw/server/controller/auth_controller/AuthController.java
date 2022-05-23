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
import it.polimi.ingsw.server.server_logic.GameServer;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;


public class AuthController extends SugarMessageProcessor {
    private final UsersRepository usersRepository = UsersRepository.getInstance();
    private static final Dotenv dotenv = Dotenv.configure().load();
    private final String hashedKey = DigestUtils.sha256Hex(dotenv.get("JWT_KEY"));
    private final GamesManager gamesManager;
    private final SecretKey key = Keys.hmacShaKeyFor(hashedKey.getBytes(StandardCharsets.UTF_8));

    public AuthController(GameServer gameServer) {
        this.gamesManager = new GamesManager(gameServer);
    }

    @SugarMessageHandler
    public SugarMessage signUpMsg(SugarMessage message, Peer peer) {
        var msg = (SignUpMsg) message;

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

        var hashedPassword = DigestUtils.sha256Hex(msg.password);

        try {
           if(usersRepository.getUserHashedPassword(msg.username).equals(hashedPassword)) {
               var jwt = Jwts.builder().claim("username", msg.username).signWith(key).compact();
               return new JWTMsg(jwt);
           }
        } catch (SQLException e) {
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
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getUsernameFromJWT(String jwt) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody().get("username", String.class);
    }

    @SugarMessageHandler
    public SugarMessage base(SugarMessage message, Peer peer) {
        if(isLoggedIn(message.jwt)) {
            return this.gamesManager.process(message, peer);
        }
        return new KOMsg("Not logged in, please login first");
    }
}
