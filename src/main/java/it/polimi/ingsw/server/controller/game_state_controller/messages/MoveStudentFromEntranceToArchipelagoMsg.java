package it.polimi.ingsw.server.controller.game_state_controller.messages;

        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
        import it.polimi.ingsw.server.model.game_logic.enums.Color;

        import java.util.List;

public class MoveStudentFromEntranceToArchipelagoMsg extends SugarMessage {
    public final Color student;
    public final List<Integer> archipelagoIslandCodes;

    public MoveStudentFromEntranceToArchipelagoMsg(Color student, List<Integer> archipelagoIslandCodes, String jwt) {
        super(SugarMethod.ACTION, jwt);

        this.student = student;
        this.archipelagoIslandCodes = archipelagoIslandCodes;
    }
}

