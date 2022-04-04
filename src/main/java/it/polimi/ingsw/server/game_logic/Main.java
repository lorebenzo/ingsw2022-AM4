package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.event_sourcing.EventsMapper;
import it.polimi.ingsw.server.game_logic.exceptions.GameStateInitializationFailureException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, GameStateInitializationFailureException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        EventsMapper.initialize();
        GameState gameState = new GameState(4);


        gameState.createSnapshot();
        gameState.modifyPlayers();
//        gameState.createSnapshot();

        GameState gamestate2 = (GameState) GameState.loadFromUuid(gameState.id);

        System.out.println(gamestate2.numberOfPlayers);
        System.out.println(gamestate2.version);
    }
}
