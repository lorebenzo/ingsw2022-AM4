package it.polimi.ingsw.client.new_gui.input_handler;

import it.polimi.ingsw.client.new_gui.GUI;

import java.util.LinkedList;
import java.util.List;

public class InputHandler {
    private static List<InputEvent> inputEvents = new LinkedList<>();

    public static void add(InputEvent inputEvent) {
        if(!GUI.currentView.equals(GUI.View.EnemiesView) /* do not listen to events in enemy view */) {
            inputEvents.add(inputEvent);

            System.out.println(inputEvent);
        }
    }
}
