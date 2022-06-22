package it.polimi.ingsw.server.controller.games_manager.messages.enums;

public enum ReturnMessage {
    JOIN_MATCHMAKING_SUCCESS("Entered successfully matchmaking."),
    JOIN_GAME_SUCCESS("Successfully joined a game."),
    DELETING_GAME("Deleting game: a player disconnected."),

    YOU_WIN("Congratulations, you won!"),
    YOU_LOSE("You lost.")

    ;

    public final String text;

    ReturnMessage(String text) {
        this.text = text;
    }
}
