package it.polimi.ingsw.client.enums;

public enum CLICommand {
    join_matchmaking(2),
    play_card(1),
    mv_std_to_dining(1),
    mv_std_to_island(2),
    mv_mother_nature(1),
    grab_std_cloud(1),
    end_turn,
    rollback,
    help,
    ;

    public int numberOfParams;

    CLICommand(int numberOfParams) {
        this.numberOfParams = numberOfParams;
    }

    CLICommand() {
        this(0);
    }

    public String text() {
        return this.toString().replaceAll("_", "-");
    }
}
