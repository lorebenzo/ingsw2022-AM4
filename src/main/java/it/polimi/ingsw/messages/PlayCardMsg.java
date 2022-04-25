package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.server.game_logic.enums.Card;

public class PlayCardMsg extends SugarMessage {
    public final Card card;

    public PlayCardMsg(Card card) {
        super(SugarMethod.ACTION);
        this.card = card;
    }
}
