package it.polimi.ingsw.server.controller.auth_controller;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.server.controller.auth_controller.messages.JWTMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.LoginMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.SignUpMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.KOMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKMsg;
import it.polimi.ingsw.server.server_logic.GameServer;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class AuthControllerTest {
    private static boolean setUpIsDone = false;
    private static AuthController authController = null;
    private static final Peer testPeer = new Peer(new Socket());

    @Before
    public void setUp() {
        if (setUpIsDone) {
            return;
        }

        try {
            GameServer gameServer = new GameServer();
            authController = new AuthController(gameServer);
            Field usersRepository = AuthController.class.getDeclaredField("usersRepository");
            UsersRepositoryMock usersRepositoryMock = new UsersRepositoryMock();
            usersRepository.setAccessible(true);
            usersRepository.set(authController, usersRepositoryMock);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        setUpIsDone = true;
    }

    @Test
    public void testSignUpWithEmptyUsername() {
        // Arrange
        var signupMessage = new SignUpMsg("", "password");

        // Act
        var returnMessage = authController.signUpMsg(signupMessage, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), KOMsg.class);
    }


    @Test
    public void testSignupCorrect() {
        // Arrange
        var signupMessage = new SignUpMsg("test", "password");

        // Act
        var returnMessage = authController.signUpMsg(signupMessage, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), OKMsg.class);
    }

    @Test
    public void testSignupWithNullUsernameOrPassword() {
        // Arrange
        var messageWithNullUsername = new SignUpMsg(null, "password");
        var messageWithNullPassword = new SignUpMsg("prova", null);

        // Act
        var returnMessage = authController.signUpMsg(messageWithNullUsername, testPeer);
        var returnMessagePassword = authController.signUpMsg(messageWithNullPassword, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), KOMsg.class);
        assertEquals(returnMessagePassword.getClass(), KOMsg.class);
    }

    @Test
    public void testLoginWithWrongPasswordUsername() {
        // Arrange
        var messageWithNullUsername = new LoginMsg(null, "password");
        var messageWithNullPassword = new LoginMsg("prova", null);

        // Act
        var returnMessage = authController.loginMsg(messageWithNullUsername, testPeer);
        var returnMessagePassword = authController.loginMsg(messageWithNullPassword, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), KOMsg.class);
        assertEquals(returnMessagePassword.getClass(), KOMsg.class);
    }

    @Test
    public void testLoginCorrect() {
        // Arrange
        var message = new LoginMsg("prova", "password");

        // Act
        var returnMessage = authController.loginMsg(message, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), JWTMsg.class);
    }

    @Test
    public void testWrongLogin() {
        // Arrange
        var message = new LoginMsg("prova", "aaaaa");

        // Act
        var returnMessage = authController.loginMsg(message, testPeer);

        // Assert
        assertEquals(returnMessage.getClass(), KOMsg.class);
    }

    @Test
    public void getUsernameFromClaims() {
        // Arrange
        Dotenv dotenv = Dotenv.configure().load();
        String hashedKey = DigestUtils.sha256Hex(dotenv.get("JWT_KEY"));
        SecretKey key = Keys.hmacShaKeyFor(hashedKey.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().claim("username", "username").signWith(key).compact();
        String jwt2 = Jwts.builder().claim("failed", "username").signWith(key).compact();

        // Assert
        assertThrows(IllegalArgumentException.class, () -> AuthController.getUsernameFromJWT(null));
        assertThrows(IllegalArgumentException.class, () -> AuthController.getUsernameFromJWT(""));
        assertThrows(IllegalArgumentException.class, () -> AuthController.getUsernameFromJWT("eeeeeee"));
        assertEquals("username", AuthController.getUsernameFromJWT(jwt));
        assertNull(AuthController.getUsernameFromJWT(jwt2));
    }
}
