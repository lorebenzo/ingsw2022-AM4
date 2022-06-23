package it.polimi.ingsw.client.new_gui.views.matchmaking_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MatchMakingRenderer {
    public static Pane renderMatchMaking() {
        var pane = new VBox();

        var mode = new Text("Mode:");
        var radio = new RadioButton("Expert");

        var numberOfPlayers = new Text("number of players:");
        var toggleGroup = new ToggleGroup();
        var radio2 = new RadioButton("2");
        var radio3 = new RadioButton("3");
        var radio4 = new RadioButton("4");
        radio2.setToggleGroup(toggleGroup);
        radio3.setToggleGroup(toggleGroup);
        radio4.setToggleGroup(toggleGroup);

        var joinMatchMaking = new Button("JOIN MATCHMAKING");

        joinMatchMaking.setOnMouseClicked(mouseEvent -> {
            var toggled = toggleGroup.getSelectedToggle();
            int nPlayers = radio2.equals(toggled) ? 2 : radio3.equals(toggled) ? 3 : 4;

            InputHandler.add(new InputEvent(
                    InputEventType.JoinMatchMaking,
                    new InputParams()
                            .isExpert(radio.isSelected())
                            .numberOfPlayers(nPlayers)
            ));
        });

        var rejoinButton = new Button("REJOIN");

        rejoinButton.setLayoutX(GUI.SizeHandler.getX(50));
        rejoinButton.setLayoutY(GUI.SizeHandler.getY(50));

        rejoinButton.setOnMouseClicked(mouseEvent -> InputHandler.add(
                new InputEvent(
                        InputEventType.RejoinClicked,
                        new InputParams()
                )
        ));

        pane.getChildren().addAll(mode, radio, numberOfPlayers, radio2, radio3, radio4, joinMatchMaking, rejoinButton);

        return pane;
    }
}
