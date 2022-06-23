package it.polimi.ingsw.server.controller.games_manager;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.auth_controller.UsersRepositoryMock;
import it.polimi.ingsw.server.controller.games_manager.messages.GamesUpdateMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.GetGamesMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.ReJoinMsg;
import it.polimi.ingsw.server.server_logic.GameServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;

import static org.junit.Assert.*;

public class GamesManagerTests {
    private static boolean setUpIsDone = false;
    private static AuthController authController = null;

    @Before
    public void setUp() {
        if (setUpIsDone) {
            return;
        }

        try {
            authController = new AuthController(new SugarServer() {
                @Override
                protected void onPeerConnect(Peer peer) {

                }

                @Override
                protected void onPeerDisconnect(Peer peer) {

                }

                @Override
                protected void onPeerMessage(Peer peer, SugarMessage message) {

                }
            });
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
    public void testGetGames() {
        Peer firstPlayer = new Peer(new Socket());
        Peer secondPlayer = new Peer(new Socket());
        var firstUserJWT = AuthController.createFakeJWT("user1");
        var secondUserJWT = AuthController.createFakeJWT("user2");
        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        authController.base(new JoinMatchMakingMsg(2, false, secondUserJWT), secondPlayer);

        var games = (GamesUpdateMsg) authController.base(new GetGamesMsg(firstUserJWT), firstPlayer);
        assertNotNull(games.gameUUID);
    }

    @Test
    /*
     * Join a match and then rejoin the matchmaking, asserts that it has not running games
     */
    public void testRemovePlayerFromGame() {
        Peer firstPlayer = new Peer(new Socket());
        Peer secondPlayer = new Peer(new Socket());
        var firstUserJWT = AuthController.createFakeJWT("user1");
        var secondUserJWT = AuthController.createFakeJWT("user2");
        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        authController.base(new JoinMatchMakingMsg(2, false, secondUserJWT), secondPlayer);

        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        var games = (GamesUpdateMsg) authController.base(new GetGamesMsg(firstUserJWT), firstPlayer);
        assertNull(games.gameUUID);
    }

    @Test
    public void testRejoinMatch() {
        Peer firstPlayer = new Peer(new Socket());
        Peer secondPlayer = new Peer(new Socket());
        var firstUserJWT = AuthController.createFakeJWT("user1");
        var secondUserJWT = AuthController.createFakeJWT("user2");
        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        authController.base(new JoinMatchMakingMsg(2, false, secondUserJWT), secondPlayer);

        authController.base(new ReJoinMsg(firstUserJWT), firstPlayer);
        var games = (GamesUpdateMsg) authController.base(new GetGamesMsg(firstUserJWT), firstPlayer);
        assertNotNull(games.gameUUID);


        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        authController.base(new ReJoinMsg(firstUserJWT), firstPlayer);
        games = (GamesUpdateMsg) authController.base(new GetGamesMsg(firstUserJWT), firstPlayer);
        assertNull(games.gameUUID);
    }
}
