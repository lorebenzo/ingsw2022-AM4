package it.polimi.ingsw.communication.sugar_framework;

import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.tcp_client.TcpClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;

import java.io.IOException;
import java.util.UUID;

public abstract class SugarClient<Context> extends TcpClient {
    private static final int    port = 33400;
    private UUID                upi = null;
    private final Context       context;


    public SugarClient(String hostname, Context context) {
        super(hostname, port);
        this.context = context;
        this.setLogHeader("[Sugar Client] : ");
    }

    @Override
    protected void onConnect() {
        super.onConnect();
    }

    @Override
    protected void onDisconnect()  {
        super.onDisconnect();
    }

    @Override
    protected final void onMessage(String input) {
        super.onMessage(input);
        this.log("input: " + input);
    }

    public void send(SugarMessage message) throws DisconnectionException {
        try {
            this.send(message.serialize());
        } catch (IOException e) {
            this.disconnect();
        }
    }

    public void setUpi(UUID upi) {
        this.upi = upi;
    }
}
