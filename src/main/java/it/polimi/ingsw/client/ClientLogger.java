package it.polimi.ingsw.client;

public class ClientLogger implements Logger {
    @Override
    public void logSuccess(String s) {
        System.out.println(s);
    }

    @Override
    public void logError(String s) {
        System.out.println(s);
    }
}
