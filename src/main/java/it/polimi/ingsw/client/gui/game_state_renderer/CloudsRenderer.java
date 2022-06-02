package it.polimi.ingsw.client.gui.game_state_renderer;

import it.polimi.ingsw.client.gui.UserInputHandler;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class CloudsRenderer {
    public static VBox renderClouds(List<List<Color>> clouds) {
        var cloudsBox = new VBox();

        cloudsBox.getChildren().add(new Text("Clouds"));

        var cloudsElement = new HBox();

        // Add style
        cloudsElement.setStyle("-fx-alignment: center");

        for(int i = 0; i < clouds.size(); i++) {
            var cloudElement = new VBox();

            // Set style
            cloudElement.setStyle("-fx-padding: 10");

            // Add event listener
            int finalI = i;
            cloudElement.setOnMouseClicked(mouseEvent -> UserInputHandler.onCloudClick(finalI, clouds.get(finalI)));

            // Header
            var header = new Text(Integer.toString(i));
            cloudElement.getChildren().add(header);

            // Students
            var studentsBox = new HBox();
            for(var student : clouds.get(i)) {
                var text = new Text(GameStateRenderer.studentSymbol + " ");
                text.setFill(GameStateRenderer.colorMap.get(student));
                studentsBox.getChildren().add(text);
            }
            cloudElement.getChildren().add(studentsBox);

            cloudsElement.getChildren().add(cloudElement);
        }

        cloudsBox.getChildren().add(cloudsElement);

        // Add style
        cloudsBox.setStyle("-fx-alignment: center");

        return cloudsBox;
    }
}
