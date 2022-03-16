package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.game_logic.exceptions.GameStateInitializationFailureException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameState {
    private final List<Archipelago> archipelagos;
    private final List<SchoolBoard> schoolBoards;
    private final List<List<Color>> clouds;
    private final StudentFactory studentFactory;
    private final int numberOfPlayers;

    /**
     *
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        if(numberOfPlayers < 2 || numberOfPlayers > 4) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.archipelagos = this.initializeArchipelagos();
        this.studentFactory = new StudentFactory();
        try {
            this.schoolBoards = this.initializeSchoolBoards(numberOfPlayers);
            this.clouds = this.initializeClouds(numberOfPlayers);
        } catch (EmptyStudentSupplyException e) {
            e.printStackTrace();
            throw new GameStateInitializationFailureException();
        }
    }

    private List<List<Color>> initializeClouds(int numberOfPlayers) throws EmptyStudentSupplyException {
        List<List<Color>> clouds = new ArrayList<>(numberOfPlayers);

        // Create a cloud for each player
        for(int i = 0; i < numberOfPlayers; i++) {
            List<Color> cloud = new ArrayList<>(numberOfPlayers == 3 ? 4 : 3);
            cloud.addAll(this.studentFactory.getNStudents(numberOfPlayers == 3 ? 4 : 3));
            clouds.add(cloud);
        }

        return clouds;
    }

    private List<Archipelago> initializeArchipelagos() {
        List<Archipelago> archipelagos = new LinkedList<>();
        for(int i = 1; i <= 12; i++)
            archipelagos.add(new Archipelago(i));
        return archipelagos;
    }

    private List<SchoolBoard> initializeSchoolBoards(int numberOfPlayers) throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        schoolBoards.add(
                new SchoolBoard(0, this.studentFactory.getNStudents((numberOfPlayers == 3) ? 9 : 7), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(1, this.studentFactory.getNStudents((numberOfPlayers == 3) ? 9 : 7), TowerColor.BLACK)
        );
        if(numberOfPlayers == 3)
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(9), TowerColor.GRAY)
            );
        if(numberOfPlayers == 4) {
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(7), TowerColor.WHITE)
            );
            schoolBoards.add(
                    new SchoolBoard(3, this.studentFactory.getNStudents(7), TowerColor.BLACK)
            );
        }

        return schoolBoards;
    }
}
