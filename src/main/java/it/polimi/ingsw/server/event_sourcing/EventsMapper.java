package it.polimi.ingsw.server.event_sourcing;


import it.polimi.ingsw.server.model.game_logic.events.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventsMapper {
    static Map<Integer, Class<?>> mapper = new HashMap<>();
    static {
        mapper.put(0, InitGameStateEvent.class);
        mapper.put(1, PlayCardEvent.class);
        mapper.put(2, FillCloudEvent.class);
        mapper.put(3, GrabStudentsFromCloudEvent.class);
        mapper.put(4, SetCurrentPhaseEvent.class);

    }


    public static Class<?> getFromId(Integer id) {
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