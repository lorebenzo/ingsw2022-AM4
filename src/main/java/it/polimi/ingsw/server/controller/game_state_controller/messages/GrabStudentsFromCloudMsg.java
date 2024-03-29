package it.polimi.ingsw.server.controller.game_state_controller.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class GrabStudentsFromCloudMsg extends SugarMessage {
    public final int cloudIndex;

    public GrabStudentsFromCloudMsg(int cloudIndex, String jwt) {
        super(SugarMethod.ACTION, jwt);

        this.cloudIndex = cloudIndex;
    }
}

