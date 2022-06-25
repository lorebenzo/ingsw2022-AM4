package it.polimi.ingsw.client.game_client_and_cli;

import it.polimi.ingsw.client.cli_graphics.Terminal;
import it.polimi.ingsw.server.controller.games_manager.messages.ChatMsg;
import it.polimi.ingsw.server.model.game_logic.LightGameState;

public class GameLogger implements Logger {
    private final Terminal terminal;

    public GameLogger(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void logSuccess(String s) {
        this.terminal.logSuccess(s);
        this.terminal.flush();
    }

    @Override
    public void logError(String s) {
        this.terminal.logError(s);
        this.terminal.flush();
    }

    @Override
    public void log(String s) {
        this.terminal.log(s);
        this.terminal.flush();
    }

    @Override
    public void logChat(ChatMsg msg) {
        if(msg.to.equals("all"))
            this.terminal.logCyan("[ " + msg.from + " ] " + msg.message);
        else if(msg.to.equals("team"))
            this.terminal.logPurple("[ " + msg.from + " ] " + msg.message);
        else
            this.terminal.log("[ " + msg.from + " ] " + msg.message);
        this.terminal.flush();
    }

    @Override
    public void logGameState(LightGameState lightGameState) {
        this.terminal.updateGameState(lightGameState);
        this.terminal.flush();
    }
}
