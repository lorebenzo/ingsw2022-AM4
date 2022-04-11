package it.polimi.ingsw.server.communication.tcp_server;

import java.net.Socket;

public interface MessageCallback {
    void apply(Socket socket, String msg);
}
