package it.polimi.ingsw.client;

import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.client.new_gui.GUI;
import javafx.application.Platform;

public class MainGUI {
    public static void main(String[] args) {
        var gc = new GameClient();
        new Thread(gc).start();

        new Thread(() -> GUI.init(args, gc)).start();

        Platform.runLater(() -> GUI.switchView(GUI.View.LoginView));
    }
}
