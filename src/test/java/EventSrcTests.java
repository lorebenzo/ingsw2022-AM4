import it.polimi.ingsw.communication.sugar_framework.SerDes;
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

public class EventSrcTests {

    @Test
    public void testGameStateCreation() {
        try {
            var gameState = new GameState(2);
            gameState.playCard(Card.CAT);
//            gameState.createSnapshot();
//            gameState.rollback();

            FileOutputStream fileOutputStream
                    = new FileOutputStream("yourfile.txt");
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gameState);
            objectOutputStream.flush();
            objectOutputStream.close();

            FileInputStream fileInputStream
                    = new FileInputStream("yourfile.txt");
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            gameState = (GameState) objectInputStream.readObject();
            objectInputStream.close();
            gameState.playCard(Card.CAT);
            gameState.fillCloud(0);
            gameState.grabStudentsFromCloud(0);
            System.out.println(gameState.getCurrentPlayerSchoolBoardForTesting().getStudentsInTheEntrance());
            var gs = (GameState) GameState.loadFromUuid(gameState.id);
            gs.createSnapshot();
            System.out.println(gs.getCurrentPlayerSchoolBoardForTesting().getDeck());
//            System.out.println(gs.getClouds());
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert true;
    }

    @Test
    public void testSerialization() throws GameStateInitializationFailureException {
        var gs = new GameState(2);
        gs.getNextTurn();
        System.out.println(gs.isLastTurnInThisRound());

        System.out.println(SerDes.serialize(gs));
    }
}
