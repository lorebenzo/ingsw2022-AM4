package it.polimi.ingsw.server.controller.game_state_controller.messages;

        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
        import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class EndTurnMsg extends SugarMessage {

    public EndTurnMsg() {
        super(SugarMethod.ACTION);
    }
}

