package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

public class NumberOfPlayersStrategyFactory {
    /**
     * @signals IllegalArgumentException if numberOfPlayers is not in {2, 3, 4}
     * @return the correct strategy according to the numberOfPlayer provided
     */
    public static NumberOfPlayersStrategy getCorrectStrategy(int numberOfPlayers) {
        if(numberOfPlayers < 2 || numberOfPlayers > 4) throw new IllegalArgumentException();

        if (numberOfPlayers == 2) return new TwoPlayerStrategy();
        else if (numberOfPlayers == 3) return new ThreePlayerStrategy();
        else return new FourPlayerStrategy();
    }
}
