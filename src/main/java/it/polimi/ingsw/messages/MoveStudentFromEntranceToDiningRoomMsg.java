package it.polimi.ingsw.messages;

        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
        import it.polimi.ingsw.server.game_logic.enums.Color;

public class MoveStudentFromEntranceToDiningRoomMsg extends SugarMessage {
    public final Color student;

    public MoveStudentFromEntranceToDiningRoomMsg(Color student) {
        super(SugarMethod.ACTION);

        this.student = student;
    }
}
