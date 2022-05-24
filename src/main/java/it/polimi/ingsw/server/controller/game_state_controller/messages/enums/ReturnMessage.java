package it.polimi.ingsw.server.controller.game_state_controller.messages.enums;

public enum ReturnMessage {
    NOT_YOUR_TURN("It's not your turn."),
    WRONG_PHASE_EXCEPTION("The played move was played in the wrong game phase."),
    CARD_IS_NOT_IN_THE_DECK("Card played is not available in the deck."),
    INVALID_CARD_PLAYED("Card played has already been played by another player in this round."),

    TOO_MANY_STUDENTS_MOVED("You already moved the allowed number of students in this round. Choose another move."),
    STUDENT_NOT_IN_THE_ENTRANCE("The student you are trying to move is not in the entrance of your school board."),
    FULL_DINING_ROOM_LANE("The dining room lane corresponding to the color of the student you are trying to move is already full."),

    INVALID_NUMBER_OF_STEPS("The inputted number of steps is invalid. It has to be between 0 and the number of steps allowed by the card played in this round."),
    EMPTY_CLOUD("The chosen cloud is empty, choose another one."),

    MORE_STUDENTS_TO_BE_MOVED("There are students yet to be moved before ending the turn."),
    MOTHER_NATURE_TO_BE_MOVED("Mother Nature has to be moved before ending turn."),
    STUDENTS_TO_BE_GRABBED_FROM_CLOUD("You have to choose a cloud from which getting the students before ending the turn."),
    CARD_NOT_PLAYED("You have to play a card before ending your turn."),
    MERGE_PERFORMED("Archipelagos merged!"),
    MERGE_NOT_PERFORMED("Archipelagos not merged!"),
    MOVE_ALREADY_PLAYED("The selected move was already performed and cannot be repeated."),
    STUDENTS_GRABBED_FROM_CLOUD("Students successfully grabbed from cloud");

    public final String text;

    ReturnMessage(String text){
        this.text = text;
    }
}
