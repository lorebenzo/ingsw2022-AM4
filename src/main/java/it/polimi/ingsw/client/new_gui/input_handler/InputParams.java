package it.polimi.ingsw.client.new_gui.input_handler;

import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.Optional;
import java.util.function.Consumer;

public class InputParams {
    public Optional<Color> color = Optional.empty();
    public Optional<Integer> id = Optional.empty();
    public Optional<Card> card = Optional.empty();
    public Optional<String> text = Optional.empty();
    public Optional<String> username = Optional.empty();
    public Optional<String> password = Optional.empty();
    public Optional<Boolean> isExpert = Optional.empty();
    public Optional<Integer> numberOfPlayers = Optional.empty();

    // Builders
    public InputParams numberOfPlayers(int n) {
        this.numberOfPlayers = Optional.of(n);
        return this;
    }

    public InputParams isExpert(boolean b) {
        this.isExpert = Optional.of(b);
        return this;
    }

    public InputParams color(Color color) {
        this.color = Optional.of(color);
        return this;
    }

    public InputParams id(int id) {
        this.id = Optional.of(id);
        return this;
    }

    public InputParams card(Card card) {
        this.card = Optional.of(card);
        return this;
    }

    public InputParams text(String text) {
        this.text = Optional.of(text);
        return this;
    }

    public InputParams username(String username) {
        this.username = Optional.of(username);
        return this;
    }

    public InputParams password(String password) {
        this.password = Optional.of(password);
        return this;
    }

    @Override
    public String toString() {
        var stringRef = new Object() {
            String s = "";
        };

        Consumer printParam = param ->  stringRef.s += param.toString() + ", ";

        color.ifPresent(printParam);
        id.ifPresent(printParam);
        card.ifPresent(printParam);
        text.ifPresent(printParam);
        username.ifPresent(printParam);
        password.ifPresent(printParam);
        isExpert.ifPresent(printParam);
        numberOfPlayers.ifPresent(printParam);

        return stringRef.s.substring(0, Math.max(0, stringRef.s.length() - 2));
    }
}
