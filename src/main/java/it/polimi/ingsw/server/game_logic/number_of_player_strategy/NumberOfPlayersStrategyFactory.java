package it.polimi.ingsw.server.game_logic.number_of_player_strategy;

public class NumberOfPlayersStrategyFactory {
    /**
     * @throws  IllegalArgumentException if numberOfPlayers is not in {2, 3, 4}
     * @return the correct strategy according to the numberOfPlayer provided
     */
    public static NumberOfPlayersStrategy getCorrectStrategy(int numberOfPlayers) {
        return switch (numberOfPlayers) {
            case 2 -> new TwoPlayerStrategy();
            case 3 -> new ThreePlayerStrategy();
            case 4 -> new FourPlayerStrategy();
            default -> throw new IllegalArgumentException();
        };
    }
}
