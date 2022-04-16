package it.polimi.ingsw.communication.sugar_framework;

import it.polimi.ingsw.communication.sugar_framework.exceptions.MessageDeserializationException;
import it.polimi.ingsw.communication.sugar_framework.messages.HeartBeatMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMethod;
import it.polimi.ingsw.communication.tcp_client.TcpClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SugarClient extends TcpClient {
    private static final int                port = 33400;
    private UUID                            upi = null;
    private Consumer<SugarMessage>          onMessage;

    public SugarClient(String hostname, Consumer<SugarMessage> onMessage) {
        super(hostname, port);
        this.setLogHeader("[Sugar Client] : ");
        this.onMessage = onMessage;
    }

    @Override
    protected void onConnect() {}

    @Override
    protected void onDisconnect() {

    }

    @Override
    protected final void onMessage(String input) {
        try {
            var message = SugarMessage.deserialize(input);
            if(message.sugarMethod.equals(SugarMethod.HEARTBEAT)) {
                // Send heartbeat back with the same id
                this.send(new HeartBeatMessage(message.messageID));
            }
            else {
                this.onMessage.accept(message);
            }
        } catch (MessageDeserializationException e) {
            this.log("error in message deserialization, dropping message : " + input);
        } catch (DisconnectionException ignored) {}
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
