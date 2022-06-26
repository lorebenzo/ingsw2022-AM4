package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;


public class ButtonsRenderer {
    public static Pane renderEndTurnButton() {
        var endTurnButton = new Button("End Turn");

        endTurnButton.setLayoutX(GUI.SizeHandler.getX(Layout.endTurnButtonRelX));
        endTurnButton.setLayoutY(GUI.SizeHandler.getY(Layout.endTurnButtonRelY));

        endTurnButton.setOnMouseClicked(mouseEvent -> {
            InputHandler.add(new InputEvent(
                    InputEventType.EndTurn,
                    new InputParams()
            ));
        });

        return new Pane(endTurnButton);
    }

    public static Pane renderUndoButton() {
        var undoButton = new Button("Undo");

        undoButton.setLayoutX(GUI.SizeHandler.getX(Layout.undoButtonRelX));
        undoButton.setLayoutY(GUI.SizeHandler.getY(Layout.undoButtonRelY));

        undoButton.setOnMouseClicked(mouseEvent -> {
            GUI.gameClient.rollback();
        });

        return new Pane(undoButton);
    }
}
