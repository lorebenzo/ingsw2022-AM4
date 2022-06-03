package it.polimi.ingsw.server.model.game_logic.number_of_player_strategy;

public class ExpertNumberOfPlayersStrategyFactory {

    /**
     * @throws  IllegalArgumentException if numberOfPlayers is not in {2, 3, 4}
     * @return the correct strategy according to the numberOfPlayer provided
     */
    public static NumberOfPlayersStrategy getCorrectStrategy(int numberOfPlayers) {
        return switch (numberOfPlayers) {
            case 2 -> new ExpertTwoPlayerStrategy();
            case 3 -> new ExpertThreePlayerStrategy();
            case 4 -> new ExpertFourPlayerStrategy();
            default -> throw new IllegalArgumentException();
        };
    }

}
