package it.polimi.ingsw.client.cli_graphics;

import it.polimi.ingsw.server.model.game_logic.*;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;

import java.io.*;
import java.util.*;

public class Terminal {
    private int rows;
    private int cols;
    private String[][] terminal; // terminal is a matrix where each cell is a unicode character, represented by a String
    private PrintStream target;

    // Logging
    private final int loggerWidth;
    private final int loggerHeight;
    private List<UnicodeString> logs = new LinkedList<>();

    // Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREY = "\u001B[61m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private static final Map<Color, String> studentColorToANSI = new HashMap<>();
    private static final Map<TowerColor, String> towerColorToANSI = new HashMap<>();
    static {
        studentColorToANSI.put(Color.RED, ANSI_RED);
        studentColorToANSI.put(Color.CYAN, ANSI_CYAN);
        studentColorToANSI.put(Color.GREEN, ANSI_GREEN);
        studentColorToANSI.put(Color.PURPLE, ANSI_PURPLE);
        studentColorToANSI.put(Color.YELLOW, ANSI_YELLOW);

        towerColorToANSI.put(TowerColor.BLACK, ANSI_BLACK);
        towerColorToANSI.put(TowerColor.WHITE, ANSI_WHITE);
        towerColorToANSI.put(TowerColor.GRAY, ANSI_GREY);
        towerColorToANSI.put(TowerColor.NONE, "");
    }

    // Emojis
    private static final String studentSymbol = "■";//"\uD83C\uDF93";
    private static final String professorSymbol = "■";//"\uD83D\uDC68\u200D";
    private static final String towerSymbol = "T";// "♖";
    private static final String filledCircleSymbol = ">";//"⬤";


    /**
     * Minimum width 10
     * @param maxRowsNumber is the maximum number of rows that can be used in the terminal.
     * @param maxColumnsNumber is the maximum number of columns that can be used in the terminal.
     * @param target represents where the terminal will be printed.
     */
    public Terminal(int maxRowsNumber, int maxColumnsNumber, PrintStream target) {
        this.rows = maxRowsNumber;
        this.cols = maxColumnsNumber;
        this.target = target;
        this.terminal = new String[maxRowsNumber][maxColumnsNumber];
        this.loggerWidth = maxColumnsNumber/10*3; // TODO: fix bug: logs are displayed also if they go outside the log box
        this.loggerHeight = maxRowsNumber;
        this.cleanEverything();
    }

    /**
     * This method puts the given character in the given position in the terminal.
     * @param row is the parameter that indicates the row coordinate in which the given character will be written.
     * @param column is the parameter that indicates the column coordinate in which the given character will be written.
     * @param character is the character that will be written.
     */
    private void put(int row, int column, String character) {
        this.terminal[row][column] = character;
    }

    /**
     * This method gets a character in input (in the form of a String) and applies the given color to it.
     * @param s is the character that will be returned
     * @param textColorANSI is the color with which the character will be written
     * @return the given character with the given color applied to it
     */
    private String color(String s, String textColorANSI) {
        return textColorANSI + s + ANSI_RESET;
    }

    private String color(String string, String textColorANSI, String backgroundColorANSI) {
        return backgroundColorANSI + textColorANSI + string + ANSI_RESET;
    }

    /**
     * Prints the hyperString to the terminal
     * @param hyperString list of characters, represented as a list of strings
     * @param row is the row coordinate from which the printing process will start
     * @param column is the column coordinate from which the printing process will start
     */
    public void putStringAsComponent(List<String> hyperString, int row, int column) {
        final int startingColumn = column;
        for(var character : hyperString) {
            if(character.equals("\n")) {
                row++;
                column = startingColumn;
            } else {
                this.put(row, column, character);
                column++;
                if(column >= this.cols - this.loggerWidth) {
                    column = startingColumn;
                    row++;
                }
            }
        }
    }

    /**
     * This method flushes the content of the terminal to the target.
     */
    public void flush() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        StringBuilder stringBuilder = new StringBuilder();
        for(int row = 0; row < this.rows; row++) {
            for(int col = 0; col < this.cols; col++)
                stringBuilder.append(this.terminal[row][col]);
            stringBuilder.append('\n');
        }
        this.target.println(stringBuilder);
    }

    /**
     * This method cleans everything in the terminal except the logs window in the terminal.
     */
    private void clean() {
        for(int row = 0; row < this.rows; row++)
            for(int col = 0; col < this.cols - this.loggerWidth; col++)
                this.terminal[row][col] = " ";
    }

    /**
     * This method cleans the logs window in the terminal.
     */
    private void cleanLogs() {
        for(int row = 0; row < this.rows; row++)
            for(int col = this.cols - this.loggerWidth; col < this.cols; col++)
                this.terminal[row][col] = " ";
    }

    /**
     * This method cleans the entire terminal.
     */
    private void cleanEverything() {
        for(int row = 0; row < this.rows; row++)
            for(int col = 0; col < this.cols; col++)
                this.terminal[row][col] = " ";
    }


    // Logging

    /**
     * This method logs the given string with the default color.
     * @param stringToBeLogged is the string to be logged.
     */
    public void log(String stringToBeLogged) {
        this.logWithColor(stringToBeLogged, ANSI_RESET);
    }

    /**
     * This method logs the given string with the color indicating success.
     * @param stringToBeLogged is the string to be logged.
     */
    public void logSuccess(String stringToBeLogged) {
        this.logWithColor(stringToBeLogged, ANSI_GREY);
    }

    /**
     * This method logs the given string with the color indicating an error.
     * @param stringToBeLogged is the string to be logged.
     */
    public void logError(String stringToBeLogged) {
        this.logWithColor(stringToBeLogged, ANSI_RED);
    }

    /**
     * This method logs the given string with the cyan color.
     * @param stringToBeLogged is the string to be logged.
     */
    public void logCyan(String stringToBeLogged) { this.logWithColor(stringToBeLogged, ANSI_CYAN); }

    /**
     * This method logs the given string with the purple color.
     * @param stringToBeLogged is the string to be logged.
     */
    public void logPurple(String stringToBeLogged) { this.logWithColor(stringToBeLogged, ANSI_PURPLE); }

    /**
     * This method logs the given string with the color indicating a warning.
     * @param stringToBeLogged is the string to be logged.
     */
    public void logWarning(String stringToBeLogged) {
        this.logWithColor(stringToBeLogged, ANSI_YELLOW);
    }

    /**
     * This method logs the given string with the given color (represented with a string).
     * @param stringToBeLogged is the string to be logged.
     * @param ansiColorCode is the color in which the given string will be logged.
     */
    private void logWithColor(String stringToBeLogged, String ansiColorCode) {
        UnicodeString log = new UnicodeString();
        for(var ch : stringToBeLogged.toCharArray())
            log.appendUnicodeChar(ansiColorCode + ch + ANSI_RESET);
        this.logs.add(0, log);
        this.updateLogs(this.logs);
    }

    /**
     * This method prints the logs to the terminal.
     * @param logs represents the list of logs that will be printed to the terminal.
     */
    private void updateLogs(List<UnicodeString> logs) {
        this.cleanLogs();

        List<String> characters = new LinkedList<>();
        for(var log : logs) {
            //List<String> logCharacters = log.getCharacters();
            //List<String> logCharacters = log.getUnicodeString();

            characters.add(">");
            characters.add(" ");
            //characters.addAll(logCharacters);
            characters.addAll(log.getUnicodeString());
            characters.add("\n");
        }

        var index = 0;
        for(int row = 0; row < this.rows && index < characters.size(); row++) {
            for(int col = this.cols - this.loggerWidth; col < this.cols && index < characters.size(); col++) {
                var ch = characters.get(index++);
                if(!ch.equals("\n"))
                    this.put(row, col, ch);
                else break;
            }
        }
    }

    // TODO: implement scrolling
    /*
    public void scrollLogs(int amount) {
        var logs = new LinkedList<>(this.logs);
        for(int i = 0; i < amount && this.logs.size() - 1 - i >= 0; i++)
            this.logs.remove(this.logs.size() - 1 - i);
        this.updateLogs(logs);
    }
     */


    /**
     * This method updates the terminal with the new lightGameState.
     * @param lightGameState is the new lightGameState that will be rendered on the terminal.
     */
    public void updateGameState(LightGameState lightGameState) {
        this.clean();
        this.renderSchoolBoards(lightGameState.schoolBoards, lightGameState.usernameToSchoolBoardID, lightGameState.currentPlayerSchoolBoardId, 0, 0);
        this.renderAvailableCharacters(lightGameState.availableCharacters, 15, 85);
        this.renderArchipelagos(lightGameState.archipelagos, lightGameState.motherNaturePosition, 0, 65);
        this.renderClouds(lightGameState.clouds, 15, 65);
    }

    private void renderAvailableCharacters(List<LightPlayableCharacter> availableCharacters, int row, int col) {

        var header = new UnicodeString()
                .appendNonUnicodeString("Available Characters:")
                .getUnicodeString();
        this.putStringAsComponent(header, row++, col);

        var header2 = new UnicodeString()
                .appendNonUnicodeString("ID:    Cost:")
                .getUnicodeString();
        this.putStringAsComponent(header2, row++, col);


        for(int i = 0; i < availableCharacters.size(); i++) {
            var character = availableCharacters.get(i);

            // Display archipelago
            UnicodeString characterRepresentation = new UnicodeString();

            // Add island codes
            characterRepresentation.appendNonUnicodeString(character.characterId + "      " + character.currentCost + "      ");
            if(character.availableLocks != null)
                characterRepresentation.appendNonUnicodeString("Available locks: " + character.availableLocks);

            if(character.students != null){
                characterRepresentation.appendNonUnicodeString("Students: ");
                for(var student : character.students)
                    characterRepresentation.appendUnicodeChar(
                            this.color(studentSymbol, studentColorToANSI.get(student))
                    ).appendNonUnicodeString(" ");
            }


            // Display data
            this.putStringAsComponent(characterRepresentation.getUnicodeString(), row + i + 1, col);


        }
    }

    /**
     * This method returns the username corresponding to the given schoolBoardId wrapped in an Optional, if it exists. Optional.empty() if the provided
     * schoolBoardId doesn't exist.
     * @param usernameToSchoolBoardId is the map that maps usernames to schoolBoardIds.
     * @param schoolBoardId represents the schoolBoardId for which you want to get the username.
     * @return the username corresponding to the given schoolBoardId
     */
    private Optional<String> getUsernameFromSchoolBoardID(Map<String, Integer> usernameToSchoolBoardId, int schoolBoardId) {
        for (Map.Entry<String, Integer> username : usernameToSchoolBoardId.entrySet()) {
            if (Objects.equals(schoolBoardId, username.getValue())) {
                return Optional.of(username.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * This method renders all the schoolBoards in the terminal.
     * @param schoolBoards is a list containing the schoolBoard of every player.
     * @param usernameToSchoolBoardId is a map that maps the username to the schoolBoardId of each player.
     * @param currentPlayerSchoolBoardId indicates the schoolBoardId of the current player.
     * @param row indicates the row from which the schoolBoards will be printed.
     * @param col indicates the column from which the schoolBoards will be printed.
     */
    private void renderSchoolBoards(List<LightSchoolBoard> schoolBoards, Map<String, Integer> usernameToSchoolBoardId, int currentPlayerSchoolBoardId, int row, int col) {
        // Display schoolBoards
        int printed = 0;
        for(var schoolBoard : schoolBoards) {
            var _row = printed == 0 || printed == 1 ? row : row + 11;
            var _col = printed == 0 || printed == 2 ? col : col + 32;


            var username = getUsernameFromSchoolBoardID(usernameToSchoolBoardId, schoolBoard.id);

            // Display schoolBoard name
            var name = new UnicodeString()
                    .appendNonUnicodeString("Schoolboard " + username.get());
            if(schoolBoard.id == currentPlayerSchoolBoardId)
                name.color(ANSI_GREEN);
            this.putStringAsComponent(name.getUnicodeString(), _row++, _col);

            // Display owned professors
            var professors = new UnicodeString()
                    .appendNonUnicodeString("Professors: ");
            for(var professor : schoolBoard.professorsTable)
                professors.appendUnicodeChar(
                        this.color(professorSymbol, studentColorToANSI.get(professor))
                ).appendNonUnicodeString(" ");
            this.putStringAsComponent(professors.getUnicodeString(), _row++, _col);

            // Display dining room
            for(var color : Color.values()) {
                var diningRoomLane = new UnicodeString()
                        .appendUnicodeChar(
                                this.color(filledCircleSymbol, studentColorToANSI.get(color))
                        ).appendNonUnicodeString(": ");
                for(int i = 0; i < schoolBoard.diningRoomLaneColorToNumberOfStudents.get(color); i++)
                    diningRoomLane.appendUnicodeChar(
                            this.color(studentSymbol, studentColorToANSI.get(color))
                    ).appendNonUnicodeString(" ");
                this.putStringAsComponent(diningRoomLane.getUnicodeString(), _row++, _col);
            }

            // Display entrance
            var entrance = new UnicodeString()
                    .appendNonUnicodeString("Entrance: ");
            for(var student : schoolBoard.studentsInTheEntrance)
                entrance.appendUnicodeChar(
                        this.color(studentSymbol, studentColorToANSI.get(student))
                ).appendNonUnicodeString(" ");
            this.putStringAsComponent(entrance.getUnicodeString(), _row++, _col);

            // Display cards
            var cards = new UnicodeString()
                    .appendNonUnicodeString(
                            schoolBoard.deck
                                    .stream()
                                    .map(Card::getValue)
                                    .toList()
                                    .toString()
                    );

            this.putStringAsComponent(
                    new UnicodeString().appendNonUnicodeString("Cards:").getUnicodeString(), _row++, _col
            );
            this.putStringAsComponent(cards.getUnicodeString(), _row++, _col);

            var cardsSteps = new UnicodeString()
                    .appendNonUnicodeString(
                            schoolBoard.deck
                                    .stream()
                                    .map(Card::getSteps)
                                    .toList()
                                    .toString()
                    );

            this.putStringAsComponent(cardsSteps.getUnicodeString(), _row++, _col);

            printed++;
        }
    }


    /**
     * This method renders all the archipelagos in the terminal.
     * @param archipelagos is a list containing all the archipelagos.
     * @param motherNaturePosition is an integer that indicates the index of archipelagos in which motherNature finds itself.
     * @param row indicates the row from which the archipelagos will be printed.
     * @param col indicates the column from which the archipelagos will be printed.
     */
    private void renderArchipelagos(List<LightArchipelago> archipelagos, int motherNaturePosition, int row, int col) {
        // Display header
        var header = new UnicodeString()
                .appendNonUnicodeString("Archipelagos:")
                .getUnicodeString();
        this.putStringAsComponent(header, row, col);

        for(int i = 0; i < archipelagos.size(); i++) {
            var archipelago = archipelagos.get(i);

            // Display archipelago
            UnicodeString archipelagoRepresentation = new UnicodeString();

            // Add island codes
            archipelagoRepresentation.appendNonUnicodeString(archipelago.islandCodes.toString() + ":    ");
            if(archipelago.equals(archipelagos.get(motherNaturePosition))) archipelagoRepresentation.color(ANSI_GREEN);

            // Add towers
            String towerColor = towerColorToANSI.get(archipelago.towerColor);
            if(!archipelago.towerColor.equals(TowerColor.NONE))
                for(var islandCodes : archipelago.islandCodes)
                    archipelagoRepresentation.appendUnicodeChar(
                            this.color(towerSymbol, towerColor)
                    );
            archipelagoRepresentation.appendNonUnicodeString("  ");

            // Add students
            for(var entry : archipelago.studentToNumber.entrySet()) {
                String studentColor = studentColorToANSI.get(entry.getKey());
                for (int j = 0; j < entry.getValue(); j++) {
                    archipelagoRepresentation.appendUnicodeChar(
                            this.color(studentSymbol, studentColor)
                    ).appendNonUnicodeString(" ");
                }
            }

            // Display data
            this.putStringAsComponent(archipelagoRepresentation.getUnicodeString(), row + 1 + i, col);
        }
    }

    /**
     * This method renders all the clouds in the terminal.
     * @param clouds is a list containing all the clouds.
     * @param row indicates the row from which the clouds will be printed.
     * @param col indicates the column from which the clouds will be printed.
     */
    private void renderClouds(List<List<Color>> clouds, int row, int col) {
        this.putStringAsComponent(
                new UnicodeString().appendNonUnicodeString("Clouds").getUnicodeString(), row++, col
        );
        int clIndex = 0;
        for(var cloud : clouds) {
            var line = new UnicodeString();
            line.appendNonUnicodeString(Integer.toString(clIndex++))
                    .appendNonUnicodeString(": ");
            for(var student : cloud)
                line.appendUnicodeChar(
                            this.color(studentSymbol, studentColorToANSI.get(student))
                        ).appendNonUnicodeString(" ");
            this.putStringAsComponent(line.getUnicodeString(), row++, col);
        }
    }
}

/**
 * This class represents a string composed by unicode characters.
 */
class UnicodeString {
    private final List<String> string;

    private static final String ANSI_RESET = "\u001B[0m";

    public UnicodeString() {
        this.string = new LinkedList<>();
    }

    /**
     * This method gets a String in input and appends it to this UnicodeString.
     * @param string is the string that will be appended to this UnicodeString.
     * @return this, a UnicodeString made up with the previous UnicodeString and the newly added String.
     */
    public UnicodeString appendNonUnicodeString(String string) {
        for(var ch : string.toCharArray())
            this.string.add(Character.toString(ch));
        return this;
    }

    /**
     * This method gets a character in input (represented as a String) and appends it to this UnicodeString.
     * @param unicodeRepresentation is the string representing the character with its color.
     * @return this, a UnicodeString made up with the previoys UnicodeString and the newly added character.
     */
    public UnicodeString appendUnicodeChar(String unicodeRepresentation) {
        this.string.add(unicodeRepresentation);
        return this;
    }

    /**
     * This method returns this UnicodeString.
     * @return an ArrayList representing the current string.
     */
    public List<String> getUnicodeString() {
        return new ArrayList<>(string);
    }

    /**
     * This method gets a color in input (represented as a String) and modifies the color of this string accordingly.
     * @param ansiColorCode is the string representing the color that this string will be modified to.
     */
    public void color(String ansiColorCode) {
        this.string.set(0, ansiColorCode + this.string.get(0));
        this.string.set(this.string.size() - 1, this.string.get(this.string.size() - 1) + ANSI_RESET);
    }

    //TODO: verify that it's actually useless
/*    public List<String> getCharacters() {
        return string;
    }*/
}
