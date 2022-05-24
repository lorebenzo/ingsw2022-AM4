package it.polimi.ingsw.client.cli_graphics;

import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
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
        towerColorToANSI.put(TowerColor.GRAY, ANSI_BLUE); // TODO: find gray code
        towerColorToANSI.put(TowerColor.NONE, "");
    }

    // Emojis
    private static final String studentSymbol = "■";//"\uD83C\uDF93";
    private static final String professorSymbol = "■";//"\uD83D\uDC68\u200D";
    private static final String towerSymbol = "T";// "♖";
    private static final String filledCircleSymbol = ">";//"⬤";


    /**
     * Minimum width 10
     * @param rows
     * @param cols
     * @param target
     */
    public Terminal(int rows, int cols, PrintStream target) {
        this.rows = rows;
        this.cols = cols;
        this.target = target;
        this.terminal = new String[rows][cols];
        this.loggerWidth = cols/5; // TODO: fix bug: logs are displayed also if they go outside the log box
        this.loggerHeight = rows;
        this.cleanEverything();
    }

    private void put(int x, int y, String ch) {
        this.terminal[x][y] = ch;
    }

    private String color(String s, String textColorANSI) {
        return textColorANSI + s + ANSI_RESET;
    }

    private String color(String s, String textColorANSI, String backgroundColorANSI) {
        return backgroundColorANSI + textColorANSI + s + ANSI_RESET;
    }

    /**
     * Prints the hyperString to the terminal
     * @param hyperString list of characters, represented as a list of strings
     * @param x
     * @param y
     */
    public void putStringAsComponent(List<String> hyperString, int x, int y) {
        final var _y = y;
        for(var ch : hyperString) {
            if(ch.equals("\n")) {
                x++;
                y = _y;
            } else {
                this.put(x, y, ch);
                y++;
                if(y >= this.cols - this.loggerWidth) {
                    y = _y;
                    x++;
                }
            }
        }
    }

    /**
     * Flushes the content of the terminal to the real terminal
     */
    public void flush() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        StringBuilder s = new StringBuilder();
        for(int row = 0; row < this.rows; row++) {
            for(int col = 0; col < this.cols; col++)
                s.append(this.terminal[row][col]);
            s.append('\n');
        }
        this.target.println(s);
    }

    /**
     * Cleans everyhthing except the logs window in the terminal
     */
    private void clean() {
        for(int row = 0; row < this.rows; row++)
            for(int col = 0; col < this.cols - this.loggerWidth; col++)
                this.terminal[row][col] = " ";
    }

    /**
     * Cleans the logs window in the terminal
     */
    private void cleanLogs() {
        for(int row = 0; row < this.rows; row++)
            for(int col = this.cols - this.loggerWidth; col < this.cols; col++)
                this.terminal[row][col] = " ";
    }

    /**
     * Cleans the entire terminal
     */
    private void cleanEverything() {
        for(int row = 0; row < this.rows; row++)
            for(int col = 0; col < this.cols; col++)
                this.terminal[row][col] = " ";
    }


    // Logging

    /**
     * Logs the string s
     * @param s
     */
    public void log(String s) {
        this.logWithColor(s, ANSI_RESET);
    }

    public void logSuccess(String s) {
        this.logWithColor(s, ANSI_GREEN);
    }

    public void logError(String s) {
        this.logWithColor(s, ANSI_RED);
    }

    public void logWarning(String s) {
        this.logWithColor(s, ANSI_YELLOW);
    }

    private void logWithColor(String s, String ansiColorCode) {
        UnicodeString log = new UnicodeString();
        for(var ch : s.toCharArray())
            log.appendUnicodeChar(ansiColorCode + ch + ANSI_RESET);
        this.logs.add(0, log);
        this.updateLogs(this.logs);
    }

    /**
     * Prints the logs to the terminal
     * @param logs
     */
    private void updateLogs(List<UnicodeString> logs) {
        this.cleanLogs();

        List<String> characters = new LinkedList<>();
        for(var log : logs) {
            List<String> logCharacters = log.getCharacters();

            characters.add(">");
            characters.add(" ");
            for(var ch : logCharacters)
                characters.add(ch);
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


    // Print Game State
    public void updateGS(LightGameState lightGameState) {
        this.clean();
        this.renderSchoolBoards(lightGameState.schoolBoards, lightGameState.currentPlayerSchoolBoardId, 0, 0);
        this.renderArchipelagos(lightGameState.archipelagos, lightGameState.motherNaturePosition, 0, 65);
        this.renderClouds(lightGameState.clouds, 15, 65);
    }

    private void renderSchoolBoards(List<SchoolBoard> schoolBoards, int currentPlayerSchId, int row, int col) {
        // Display schoolBoards
        int printed = 0;
        for(var schoolBoard : schoolBoards) {
            var _row = printed == 0 || printed == 1 ? row : row + 11;
            var _col = printed == 0 || printed == 2 ? col : col + 32;

            // Display schoolBoard name
            var name = new UnicodeString()
                    .appendNonUnicodeString("Schoolboard " + Integer.toString(schoolBoard.getId()));
            if(schoolBoard.getId() == currentPlayerSchId)
                name.color(ANSI_GREEN);
            this.putStringAsComponent(name.getUnicodeString(), _row++, _col);

            // Display owned professors
            var professors = new UnicodeString()
                    .appendNonUnicodeString("Professors: ");
            for(var professor : schoolBoard.getProfessors())
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
                for(int i = 0; i < schoolBoard.getDiningRoomLaneColorToNumberOfStudents().get(color); i++)
                    diningRoomLane.appendUnicodeChar(
                            this.color(studentSymbol, studentColorToANSI.get(color))
                    ).appendNonUnicodeString(" ");
                this.putStringAsComponent(diningRoomLane.getUnicodeString(), _row++, _col);
            }

            // Display entrance
            var entrance = new UnicodeString()
                    .appendNonUnicodeString("Entrance: ");
            for(var student : schoolBoard.getStudentsInTheEntrance())
                entrance.appendUnicodeChar(
                        this.color(studentSymbol, studentColorToANSI.get(student))
                ).appendNonUnicodeString(" ");
            this.putStringAsComponent(entrance.getUnicodeString(), _row++, _col);

            // Display cards
            var cards = new UnicodeString()
                    .appendNonUnicodeString(
                            schoolBoard.getDeck()
                                    .stream()
                                    .map(Card::getValue)
                                    .toList()
                                    .toString()
                    );
            this.putStringAsComponent(
                    new UnicodeString().appendNonUnicodeString("Cards:").getUnicodeString(), _row++, _col
            );
            this.putStringAsComponent(cards.getUnicodeString(), _row++, _col);

            printed++;
        }
    }


    private void renderArchipelagos(List<Archipelago> archipelagos, Archipelago motherNatPos, int row, int col) {
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
            archipelagoRepresentation.appendNonUnicodeString(archipelago.getIslandCodes().toString() + ": ");
            if(archipelago.equals(motherNatPos)) archipelagoRepresentation.color(ANSI_GREEN); // TODO: fix, does not work

            // Add towers
            String towerColor = towerColorToANSI.get(archipelago.getTowerColor());
            if(!archipelago.getTowerColor().equals(TowerColor.NONE))
                for(var islandCodes : archipelago.getIslandCodes())
                    archipelagoRepresentation.appendUnicodeChar(
                            this.color(towerSymbol, towerColor)
                    );
            archipelagoRepresentation.appendNonUnicodeString("  ");

            // Add students
            for(var student : archipelago.getStudents()) {
                String studentColor = studentColorToANSI.get(student);
                archipelagoRepresentation.appendUnicodeChar(
                        this.color(studentSymbol, studentColor)
                ).appendNonUnicodeString(" ");
            }

            // Display data
            this.putStringAsComponent(archipelagoRepresentation.getUnicodeString(), row + 1 + i, col);
        }
    }

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
 * Represents a unicode string
 */
class UnicodeString {
    private final List<String> string;

    private static final String ANSI_RESET = "\u001B[0m";

    public UnicodeString() {
        this.string = new LinkedList<>();
    }

    public UnicodeString appendNonUnicodeString(String s) {
        for(var ch : s.toCharArray())
            this.string.add(Character.toString(ch));
        return this;
    }

    public UnicodeString appendUnicodeChar(String unicodeRepresentation) {
        this.string.add(unicodeRepresentation);
        return this;
    }

    public List<String> getUnicodeString() {
        return new ArrayList<>(string);
    }

    public void color(String ansiColorCode) {
        this.string.set(0, ansiColorCode + this.string.get(0));
        this.string.set(this.string.size() - 1, this.string.get(this.string.size() - 1) + ANSI_RESET);
    }

    public List<String> getCharacters() {
        return string;
    }
}
