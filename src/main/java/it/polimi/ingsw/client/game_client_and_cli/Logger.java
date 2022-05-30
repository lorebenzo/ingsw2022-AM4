package it.polimi.ingsw.client.game_client_and_cli;

import it.polimi.ingsw.server.model.game_logic.LightGameState;

public interface Logger {
    void logSuccess(String s);
    void logError(String s);
    void log(String s);
    void logGameState(LightGameState lightGameState);
}
