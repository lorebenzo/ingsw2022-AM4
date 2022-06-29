package it.polimi.ingsw.client.enums;

public enum CLICommand {
    join_matchmaking(2),
    play_card(1),
    mv_std_dining(1),
    mv_std_island(2),
    mv_mother_nature(1),
    grab_std(1),
    login(2),
    signup(2),
    play_char(3),
    chat(3),
    end_turn,
    rollback,
    rejoin,
    help,
    characters_info
    ;

    public final int numberOfParams;

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
