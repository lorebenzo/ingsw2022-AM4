package it.polimi.ingsw.server.event_sourcing;


import it.polimi.ingsw.server.model.game_logic.events.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventsMapper {
    private static final Map<Integer, Class<? extends Event>> mapper = new HashMap<>();
    static {
        mapper.put(0, InitGameStateEvent.class);
        mapper.put(1, PlayCardEvent.class);
        mapper.put(2, FillCloudEvent.class);
        mapper.put(3, GrabStudentsFromCloudEvent.class);
        mapper.put(4, SetCurrentPhaseEvent.class);
        mapper.put(5, AssignProfessorEvent.class);
        mapper.put(6, ConquerArchipelagoEvent.class);
        mapper.put(7, GetNextTurnEvent.class);
        mapper.put(8, IncreaseRoundCountEvent.class);
        mapper.put(9, MergeWithNextEvent.class);
        mapper.put(10, MergeWithPreviousEvent.class);
        mapper.put(11, MoveMotherNatureNStepsClockwiseEvent.class);
        mapper.put(12, MoveStudentFromEntranceToArchipelagoEvent.class);
        mapper.put(13, MoveStudentsFromEntranceToDiningEvent.class);
        mapper.put(14, ResetRoundIteratorEvent.class);
        mapper.put(15, ResetSchoolBoardIdsToCardsPlayerThisRoundEvent.class);
        mapper.put(16, SetActionPhaseSubTurnEvent.class);
        mapper.put(17, SetCurrentPlayerSchoolBoardIDEvent.class);
        mapper.put(18, SetRoundOrderEvent.class);
    }


    public static Class<?> getFromId(Integer id) {
        return mapper.get(id);
    }

    public static Integer getKeyByValue(Class<?> eventClass) {
        for (Map.Entry<Integer, Class<? extends Event>> eventType : mapper.entrySet()) {
            if (Objects.equals(eventClass, eventType.getValue())) {
                return eventType.getKey();
            }
        }
        return null;
    }
}