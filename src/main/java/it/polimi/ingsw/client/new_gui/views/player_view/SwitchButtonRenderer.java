package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class SwitchButtonRenderer {
    public static Pane renderSwitchButton(String text, GUI.Rectangle dstRect, GUI.View nextView) {
        var pane = new Pane();

        var switchButton = new Button(text);
        switchButton.setLayoutX(GUI.SizeHandler.getX(dstRect.relX));
        switchButton.setLayoutY(GUI.SizeHandler.getY(dstRect.relY));
        switchButton.setMaxWidth(GUI.SizeHandler.getX(dstRect.relW));
        switchButton.setMaxHeight(GUI.SizeHandler.getY(dstRect.relH));

        pane.getChildren().add(switchButton);

        // Event listeners
        switchButton.setOnMouseClicked(mouseEvent -> {
            UserExperience.playSound(AssetHolder.mouseClickSound);
            GUI.switchView(nextView);
        });

        return pane;
    }
}
