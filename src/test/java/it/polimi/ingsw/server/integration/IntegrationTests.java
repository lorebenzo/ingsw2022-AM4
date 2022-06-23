package it.polimi.ingsw.server.integration;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.SugarServer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.auth_controller.UsersRepositoryMock;
import it.polimi.ingsw.server.controller.game_state_controller.messages.MoveStudentFromEntranceToDiningRoomMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.PlayCardMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.utils.Randomizer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Random;

import static org.junit.Assert.fail;

public class IntegrationTests {

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
            fail();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        setUpIsDone = true;
    }

    @Test
    public void testOneCompleteTurn() {
        Peer firstPlayer = new Peer(new Socket());
        Peer secondPlayer = new Peer(new Socket());
        var firstUserJWT = AuthController.createFakeJWT("user1");
        var secondUserJWT = AuthController.createFakeJWT("user2");
        authController.base(new JoinMatchMakingMsg(2, false, firstUserJWT), firstPlayer);
        authController.base(new JoinMatchMakingMsg(2, false, secondUserJWT), secondPlayer);

        authController.base(new PlayCardMsg(Card.CAT, firstUserJWT), firstPlayer);
        authController.base(new PlayCardMsg(Card.FOX, secondUserJWT), secondPlayer);

        int cardPlayed = 0;

        while (cardPlayed < 3) {
            Color randomColor = Color.values()[new Random().nextInt(Color.values().length)];
            var message = (OKMsg) authController.base(new MoveStudentFromEntranceToDiningRoomMsg(randomColor, firstUserJWT), firstPlayer);
            if(message != null && message.getClass().equals(OKMsg.class))
                cardPlayed++;
        }
    }
}
