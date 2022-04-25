package it.polimi.ingsw.messages;

        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
        import it.polimi.ingsw.server.game_logic.enums.Color;

        import java.util.List;

public class MoveStudentFromEntranceToArchipelagoMsg extends SugarMessage {
    public final Color student;
    public final List<Integer> archipelagoIslandCodes;

    public MoveStudentFromEntranceToArchipelagoMsg(Color student, List<Integer> archipelagoIslandCodes) {
        super(SugarMethod.ACTION);

        this.student = student;
        this.archipelagoIslandCodes = archipelagoIslandCodes;
    }
}

