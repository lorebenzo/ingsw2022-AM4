package it.polimi.ingsw.server.communication.tcp_server;

import java.net.Socket;

public interface SocketCallBack {
    void apply(Socket socket);
}
