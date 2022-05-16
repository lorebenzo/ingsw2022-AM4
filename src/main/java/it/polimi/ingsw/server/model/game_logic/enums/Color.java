package it.polimi.ingsw.server.model.game_logic.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Color {
    GREEN, YELLOW, RED, PURPLE, CYAN
    ;

    public static Optional<Color> fromString(String color) {
        for(var _color : Color.values())
            if(_color.toString().toLowerCase().equals(color.toLowerCase()))
                return Optional.of(_color);
        return Optional.empty();
    }
}
