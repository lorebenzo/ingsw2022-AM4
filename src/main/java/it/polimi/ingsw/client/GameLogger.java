package it.polimi.ingsw.client;

import java.io.PrintStream;

public class GameLogger implements Logger {
    private final PrintStream outStream;

    public GameLogger(PrintStream outStream) {
        this.outStream = outStream;
    }

    @Override
    public void logSuccess(String s) {
        outStream.println(s);
    }

    @Override
    public void logError(String s) {
        outStream.println(s);
    }
}
