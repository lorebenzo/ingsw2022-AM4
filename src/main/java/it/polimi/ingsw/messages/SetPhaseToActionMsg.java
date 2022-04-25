package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class SetPhaseToActionMsg extends SugarMessage{
    public SetPhaseToActionMsg(){
        super(SugarMethod.ACTION);
    }
}
