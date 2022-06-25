package it.polimi.ingsw.client.new_gui.input_handler;

import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class InputParams {
    public List<Color> colors = new LinkedList<>();
    public Optional<Integer> id = Optional.empty();
    public Optional<Card> card = Optional.empty();
    public Optional<String> text = Optional.empty();
    public Optional<String> username = Optional.empty();
    public Optional<String> password = Optional.empty();
    public Optional<Boolean> isExpert = Optional.empty();
    public Optional<Integer> numberOfPlayers = Optional.empty();

    // Expert mode
    public List<Color> studentsFromEntrance = new LinkedList<>();
    public List<Color> studentsFromDining = new LinkedList<>();

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
        this.colors.add(color);
        return this;
    }

    public InputParams studentFromEntrance(Color color) {
        this.studentsFromEntrance.add(color);
        return this;
    }

    public InputParams studentFromDining(Color color) {
        this.studentsFromDining.add(color);
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

        id.ifPresent(printParam);
        if(!colors.isEmpty()) printParam.accept(colors);
        if(!studentsFromEntrance.isEmpty()) printParam.accept(studentsFromEntrance);
        if(!studentsFromDining.isEmpty()) printParam.accept(studentsFromDining);
        card.ifPresent(printParam);
        text.ifPresent(printParam);
        username.ifPresent(printParam);
        password.ifPresent(printParam);
        isExpert.ifPresent(printParam);
        numberOfPlayers.ifPresent(printParam);

        return stringRef.s.substring(0, Math.max(0, stringRef.s.length() - 2));
    }
}
