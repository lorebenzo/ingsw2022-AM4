import it.polimi.ingsw.communication.sugar_framework.SerDes;
import it.polimi.ingsw.communication.sugar_framework.exceptions.MessageDeserializationException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.CardIsNotInTheDeckException;
import it.polimi.ingsw.server.controller.game_state_controller.exceptions.InvalidCardPlayedException;
import it.polimi.ingsw.server.event_sourcing.EventsMapper;
import it.polimi.ingsw.server.model.game_logic.GameState;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import it.polimi.ingsw.server.repository.exceptions.DBQueryException;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.UUID;

public class EventSrcTests {

    @Test
    public void testGameStateCreation() {
        try {
            var gameState = new GameState(2);
            gameState.playCard(Card.CAT);

            gameState.grabStudentsFromCloud(0);
            var gs = (GameState) GameState.loadFromUuid(gameState.id);
            gs.createSnapshot();
            System.out.println(gs.getCurrentPlayerSchoolBoardForTesting().getDeck());
            System.out.println(gameState.getCurrentPlayerSchoolBoardForTesting().getDeck());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert true;
    }

    @Test
    public void testSerialization() throws GameStateInitializationFailureException, MessageDeserializationException, InvalidCardPlayedException, CardIsNotInTheDeckException, DBQueryException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        var gs = new GameState(2);
        gs.createSnapshot();
        gs.playCard(Card.CAT);
        gs.playCard(Card.DOG);


        gs = (GameState) gs.rollback();
        gs.playCard(Card.CAT);
        gs.playCard(Card.DOG);
    }
}
