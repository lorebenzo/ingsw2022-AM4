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
    private Archipelago motherNaturePosition;
    private final int numberOfPlayers;
    private final int numberOfStudentsInEachCloud;
    private final int numberOfStudentsInTheEntrance;

    /**
     *
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException {
        if(numberOfPlayers < 2 || numberOfPlayers > 4) throw new IllegalArgumentException();

        this.numberOfPlayers = numberOfPlayers;
        this.numberOfStudentsInEachCloud = numberOfPlayers == 3 ? 4 : 3;
        this.numberOfStudentsInTheEntrance = numberOfPlayers == 3 ? 9 : 7;
        this.studentFactory = new StudentFactory();
        try {
            this.archipelagos = this.initializeArchipelagos();
            this.schoolBoards = this.initializeSchoolBoards();
            this.clouds = this.initializeClouds();
        } catch (EmptyStudentSupplyException e) {
            e.printStackTrace();
            throw new GameStateInitializationFailureException();
        }
    }

    private List<List<Color>> initializeClouds() throws EmptyStudentSupplyException {
        List<List<Color>> clouds = new ArrayList<>(this.numberOfPlayers);

        // Create a cloud for each player
        for(int i = 0; i < this.numberOfPlayers; i++) {
            List<Color> cloud = new ArrayList<>(this.numberOfStudentsInEachCloud);
            cloud.addAll(this.studentFactory.getNStudents(this.numberOfStudentsInEachCloud));
            clouds.add(cloud);
        }

        return clouds;
    }

    private List<Archipelago> initializeArchipelagos() throws EmptyStudentSupplyException {
        List<Archipelago> archipelagos = new LinkedList<>();
        for(int i = 1; i <= 12; i++) {
            Archipelago newArchipelago = new Archipelago(i);
            if(i == 1) this.motherNaturePosition = newArchipelago;
            if(i != 1 && i != 6) newArchipelago.addStudent(this.studentFactory.getStudent());
            archipelagos.add(newArchipelago);
        }
        return archipelagos;
    }

    private List<SchoolBoard> initializeSchoolBoards() throws EmptyStudentSupplyException {
        List<SchoolBoard> schoolBoards = new ArrayList<>(numberOfPlayers);
        schoolBoards.add(
                new SchoolBoard(0, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
        );
        schoolBoards.add(
                new SchoolBoard(1, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
        );
        if(this.numberOfPlayers == 3)
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.GRAY)
            );
        if(this.numberOfPlayers == 4) {
            schoolBoards.add(
                    new SchoolBoard(2, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.WHITE)
            );
            schoolBoards.add(
                    new SchoolBoard(3, this.studentFactory.getNStudents(this.numberOfStudentsInTheEntrance), TowerColor.BLACK)
            );
        }

        return schoolBoards;
    }
}
