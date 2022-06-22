package it.polimi.ingsw.server.model.game_logic.utils;

import java.util.Random;

public class Randomizer {
    private static final Random randomizer = new Random();

    public static void setSeed(long seed) {
        randomizer.setSeed(seed);
    }

    public static int nextInt(int size) {
        return randomizer.nextInt(size);
    }
}
