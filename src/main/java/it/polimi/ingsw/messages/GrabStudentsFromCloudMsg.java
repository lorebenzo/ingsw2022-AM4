package it.polimi.ingsw.messages;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;

public class GrabStudentsFromCloudMsg extends SugarMessage {
    public final int cloudIndex;

    public GrabStudentsFromCloudMsg(int cloudIndex) {
        super(SugarMethod.ACTION);

        this.cloudIndex = cloudIndex;
    }
}

