package it.polimi.ingsw.client;

import it.polimi.ingsw.communication.sugar_framework.SugarClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;
import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.messages.KOMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.MoveMotherNatureMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.OKMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;


public class GameClient extends SugarMessageProcessor implements Runnable {
    private final SugarClient sugarClient;
    private final Logger logger = new ClientLogger();

    public GameClient() {
        var dotenv = Dotenv.configure().load();
        this.sugarClient = new SugarClient(dotenv.get("HOST"));
    }

    @Override
    public void run() {
        try {
            this.sugarClient.run();
        } catch (DisconnectionException ignored) { }
    }

    private void send(SugarMessage message) throws DisconnectionException {
        this.sugarClient.send(message);
    }

    // Game methods

    public void joinMatchMaking(int numberOfPlayers, boolean expertMode) {
        try {
            this.send(new JoinMatchMakingMsg(numberOfPlayers, expertMode));
        } catch (DisconnectionException e) {
            this.logger.logError(e.getMessage());
        }
    }

    /*
    public void moveMotherNature(int moves) {
        try {
            Future<?> response = this.send(new MoveMotherNatureMsg(moves));
        } catch (Exception e) {
            ...
        }
    }

     */

    /*
    public onFailureMessage(Message msg) {
        System.out.println(msg.getError());
    }

    public onOkMessage(Message msg) {
        if(containsBoard)
            print(board)
        else
            System.out.println(msg.getError());
    }

     */



    /**
     * send (msg) -> ogni messaggio in arrivo viene in messo in una queue -> parte un thread che controlla la queue e poppa
     * il msg in arrivo con id == msg.id, poi ritorna
     */

    @SugarMessageHandler
    public void OKMsg(SugarMessage message) {
        var msg = (OKMsg) message;
        logger.logSuccess(msg.text);
    }

    @SugarMessageHandler
    public void KOMsg(SugarMessage message) {
        var msg = (KOMsg) message;
        logger.logError(msg.reason);
    }
}
