package it.polimi.ingsw.server.communication;

import it.polimi.ingsw.server.communication.exceptions.ServerCreationException;
import it.polimi.ingsw.server.communication.sugar.SugarServer;
import it.polimi.ingsw.server.communication.sugar.messages.ActionMsg;
import it.polimi.ingsw.server.communication.sugar.messages.ControlMsg;
import it.polimi.ingsw.server.communication.sugar.messages.JoinMsg;
import it.polimi.ingsw.server.communication.sugar.messages.NotifyMsg;

import java.net.Socket;

public class GameServer extends SugarServer {
    public GameServer() throws ServerCreationException {
    }

    @Override
    protected void joinHandler(Socket client, JoinMsg msg) { }

    @Override
    protected void controlHandler(Socket client, ControlMsg msg) { }

    @Override
    protected void actionHandler(Socket client, ActionMsg msg) { }

    @Override
    protected void notifyHandler(Socket client, NotifyMsg msg) { }
}
