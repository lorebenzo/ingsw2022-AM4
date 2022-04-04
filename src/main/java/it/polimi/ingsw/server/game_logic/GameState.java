package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.event_sourcing.Aggregate;
import it.polimi.ingsw.server.event_sourcing.Event;
import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.enums.TowerColor;
import it.polimi.ingsw.server.game_logic.events.CreateGameStateEvent;
import it.polimi.ingsw.server.game_logic.events.CreateStudentFactoryEvent;
import it.polimi.ingsw.server.game_logic.events.InitializeSchoolBoardsAndCloudsEvent;
import it.polimi.ingsw.server.game_logic.events.ModifyNumberOfPlayersEvent;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.game_logic.exceptions.GameStateInitializationFailureException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GameState extends Aggregate {
    public List<Archipelago> archipelagos;
    public List<SchoolBoard> schoolBoards;
    public List<List<Color>> clouds;
    public StudentFactory studentFactory;
    public int numberOfPlayers;

    public GameState(UUID uuid) {
        this.id = uuid;
    }
    /**
     *
     * @param numberOfPlayers number of players in the game, must be between 2 (inclusive) and 4 (inclusive)
     */
    public GameState(int numberOfPlayers) throws GameStateInitializationFailureException, SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        UUID parentUuid = UUID.randomUUID();

        repository.storeAggregate(this.id, this.getClass().getName());
        List<Event> eventsToApply = new LinkedList<>();
        eventsToApply.add(new CreateGameStateEvent(numberOfPlayers, parentUuid));
        eventsToApply.add(new CreateStudentFactoryEvent(new StudentFactory(), parentUuid));
        eventsToApply.add(new InitializeSchoolBoardsAndCloudsEvent(numberOfPlayers, parentUuid));

        for(Event eventToApply: eventsToApply) {
            this.version++;
            repository.addEvent(this.id, eventToApply, this.version);
            this.apply(eventToApply);
        }

    }

    public void createNewGameStateHandler(CreateGameStateEvent event) throws GameStateInitializationFailureException {
        if(event.getNumberOfPlayers() < 2 || event.getNumberOfPlayers() > 4) throw new IllegalArgumentException();

        this.numberOfPlayers = event.getNumberOfPlayers();
        this.archipelagos = this.initializeArchipelagos();
    }

    public void createStudentFactoryHandler(CreateStudentFactoryEvent event) {
        this.studentFactory = event.getStudentFactory();
    }

    public void initializeSchoolBoardAndCloudsEvent(InitializeSchoolBoardsAndCloudsEvent event) throws EmptyStudentSupplyException {
        this.schoolBoards = this.initializeSchoolBoards(event.getNumberOfPlayers());
        this.clouds = this.initializeClouds(event.getNumberOfPlayers());
    }

    public void modifyPlayers() throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        UUID parentUuid = UUID.randomUUID();
        Event eventToApply = new ModifyNumberOfPlayersEvent(parentUuid, 2);
        this.version++;
        repository.addEvent(this.id, eventToApply, this.version);
        this.apply(eventToApply);
    }

    public void modifyPlayersHandler(ModifyNumberOfPlayersEvent event) {
        this.numberOfPlayers = event.getPlayers();
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
