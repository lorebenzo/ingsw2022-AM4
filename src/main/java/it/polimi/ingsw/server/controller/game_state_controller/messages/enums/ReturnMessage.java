package it.polimi.ingsw.server.controller.game_state_controller.messages.enums;

public enum ReturnMessage {
    NOT_YOUR_TURN("It's not your turn"),
    CARD_PLAYED("Card played!"),
    WRONG_PHASE("The move was played in the wrong game phase"),
    CARD_IS_NOT_IN_THE_DECK("The chosen card played is not available in the deck"),
    INVALID_CARD_PLAYED("The chosen card has already been played by another player in this round"),

    STUDENT_MOVED_DINING("Student moved to the dining room!"),
    STUDENT_MOVED_ARCHIPELAGO("Student moved to an island!"),
    TOO_MANY_STUDENTS_MOVED("You already moved the allowed number of students. Choose another move"),
    STUDENT_NOT_IN_THE_ENTRANCE("The student you are trying to move isn't in the entrance of your school board"),
    FULL_DINING_ROOM_LANE("The dining room table of the student you are trying to move is already full"),

    INVALID_NUMBER_OF_STEPS("The inputted number of steps is invalid. It has to be between 0 and the number of steps allowed by the card played in this round"),
    EMPTY_CLOUD("The chosen cloud is empty, choose another one"),

    MORE_STUDENTS_TO_BE_MOVED("There are students yet to be moved"),
    MOTHER_NATURE_TO_BE_MOVED("Mother Nature has to be moved before ending turn"),
    STUDENTS_TO_BE_GRABBED_FROM_CLOUD("You have to choose a cloud before ending the turn"),
    CARD_NOT_PLAYED("You have to play a card before ending your turn"),
    MERGE_PERFORMED("Archipelagos merged!"),
    MOVE_ALREADY_PLAYED("Move already played!"),
    MERGE_NOT_PERFORMED("Archipelagos not merged"),
    STUDENTS_GRABBED_FROM_CLOUD("Students successfully grabbed from cloud"),
    INVALID_ARCHIPELAGO_ID("The island code provided is invalid"),
    TURN_ENDED("Turn ended"),
    LAST_ROUND("This will be the final round of the game!"),
    NO_MORE_CARDS("The last card was played, therefore this is the final round of the game"),
    ROLLBACK("Successfully rolled back"),

    //EXPERT MODE
    CHARACTER_PLAYED("Character played!"),
    NOT_ENOUGH_COINS("You don't have enough coins to play the selected character"),
    INVALID_CHARACTER_INDEX("The character index you entered is invalid. Choose an ID between 0 and 2"),
    ISLAND_ALREADY_LOCKED("The island you are trying to lock is already locked"),
    NO_AVAILABLE_LOCK("There are no more locks available"),
    MOVE_NOT_AVAILABLE("The move you are trying to play is not available"),
    STUDENT_NOT_ON_CHARACTER("The student you are trying to get from the character is not available"),
    WRONG_ARGUMENTS("The character index is incompatible with the arguments provided"),
    INVALID_STUDENT_LISTS_LENGTH("The lists of students to be swapped have to be of the same length"),
    STUDENTS_NOT_IN_THE_DINING_ROOM("One or more students you are trying to swap are not present in the dining room");

    public final String text;

    ReturnMessage(String text){
        this.text = text;
    }
}
