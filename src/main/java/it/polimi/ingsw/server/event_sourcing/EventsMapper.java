package it.polimi.ingsw.server.event_sourcing;

import it.polimi.ingsw.server.game_logic.events.CreateGameStateEvent;
import it.polimi.ingsw.server.game_logic.events.CreateStudentFactoryEvent;
import it.polimi.ingsw.server.game_logic.events.InitializeSchoolBoardsAndCloudsEvent;
import it.polimi.ingsw.server.game_logic.events.ModifyNumberOfPlayersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventsMapper {
    static Map<Integer, Class<?>> mapper;

    public static void initialize() {
        mapper = new HashMap<>();
        mapper.put(0, CreateGameStateEvent.class);
        mapper.put(1, CreateStudentFactoryEvent.class);
        mapper.put(2, InitializeSchoolBoardsAndCloudsEvent.class);
        mapper.put(3, ModifyNumberOfPlayersEvent.class);
    }

    static Class<?> getFromId(Integer id) {
        return mapper.get(id);
    }

    public static Integer getKeyByValue(Class<?> eventClass) {
        for (Map.Entry<Integer, Class<?>> eventType : mapper.entrySet()) {
            if (Objects.equals(eventClass, eventType.getValue())) {
                return eventType.getKey();
            }
        }
        return null;
    }
}
