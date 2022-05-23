package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli_graphics.Terminal;
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
    public void logGameState(LightGameState lightGameState) {
        this.terminal.updateGS(lightGameState);
        this.terminal.flush();
    }
}
