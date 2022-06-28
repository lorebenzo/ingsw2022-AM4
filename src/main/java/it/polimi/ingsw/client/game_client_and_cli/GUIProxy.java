package it.polimi.ingsw.client.game_client_and_cli;

import it.polimi.ingsw.client.new_gui.GUI;
import javafx.application.Platform;

public class GUIProxy {
    public static void switchView(GUI.View view) {
        if(GUI.initialized)
            Platform.runLater(() -> GUI.switchView(view));
    }

    public static void alert(String s) {
        if(GUI.initialized) {
            Platform.runLater(() -> GUI.alert(s));
            render();
        }
    }

    public static void notify(String s) {
        if(GUI.initialized) {
            Platform.runLater(() -> GUI.notify(s));
            render();
        }
    }

    public static void render() {
        if(GUI.initialized)
            Platform.runLater(GUI::render);
    }

    public static void log(String chat) {
        if(GUI.initialized) {
            Platform.runLater(() -> GUI.log(chat));
            render();
        }
    }
}
