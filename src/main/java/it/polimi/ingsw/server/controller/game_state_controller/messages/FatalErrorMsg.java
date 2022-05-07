package it.polimi.ingsw.server.controller.game_state_controller.messages;

public class FatalErrorMsg extends KOMsg{

    public FatalErrorMsg(String reason) {
        super(reason);

        //TODO lanciare qualche tipo di eccezione da qui in modo da segnalare la terminazione forzata del gioco
    }
}
