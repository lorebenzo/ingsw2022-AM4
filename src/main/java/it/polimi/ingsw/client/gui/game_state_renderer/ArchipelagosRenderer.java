package it.polimi.ingsw.client.gui.game_state_renderer;

import it.polimi.ingsw.client.gui.UserInputHandler;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class ArchipelagosRenderer {
    static VBox renderArchipelagos(List<Archipelago> archipelagos, int motherNatureArchipelagoId) {
        var archipelagosElement = new VBox();

        // Header
        archipelagosElement.getChildren().add(new Text("Archipelagos"));

        // Bar
        var archipelagosBar = new HBox();
        for(var archipelago : archipelagos) {
            var archipelagoElement = renderArchipelago(archipelago);
            archipelagosBar.getChildren().add(archipelagoElement);

            // Archipelago event listener
            archipelagoElement.setOnMouseClicked(mouseEvent -> UserInputHandler.onArchipelagoIndexClick(
                    archipelago, getSteps(archipelagos, motherNatureArchipelagoId, archipelago.getIslandCodes().get(0)))
            );
        }

        // Bar Style
        archipelagosBar.setStyle("-fx-alignment: center");

        archipelagosElement.getChildren().add(archipelagosBar);


        // Style
        archipelagosElement.setStyle("-fx-alignment: center");

        return archipelagosElement;
    }

    private static VBox renderArchipelago(Archipelago archipelago) {
        var archipelagoElement = new VBox();

        // Header
        var header = new Text(Integer.toString(archipelago.getIslandCodes().get(0)));

        // Header Style
        header.setStyle("-fx-alignment: center");
        archipelagoElement.getChildren().add(header);

        // Students
        var students = new VBox();
        for(var color : Color.values()) {
            var colorBox = new HBox();
            var studentStream = archipelago.getStudents().stream().filter(student -> student.equals(color));
            for(var student : studentStream.toList()) {
                var stud = new Text(GameStateRenderer.studentSymbol + " ");
                stud.setFill(GameStateRenderer.colorMap.get(student));

                // Add event listener
                stud.setOnMouseClicked(mouseEvent ->
                        UserInputHandler.onStudentInArchipelagoClick(archipelago.getIslandCodes().get(0), student));

                colorBox.getChildren().add(stud);
            }
            students.getChildren().add(colorBox);
        }
        archipelagoElement.getChildren().add(students);

        // Set style
        archipelagoElement.setStyle("-fx-padding: 3");

        return archipelagoElement;
    }

    private static int getSteps(List<Archipelago> archipelagos, int motherNaturePosId, int destId) {
        int steps = 0;
        int motherNatureIndex = 0, destinationIndex = 0;
        for(int i = 0; i < archipelagos.size(); i++) {
            if(archipelagos.get(i).getIslandCodes().get(0) == motherNaturePosId)
                motherNatureIndex = i;
            if(archipelagos.get(i).getIslandCodes().get(0) == destId)
                destinationIndex = i;
        }

        return (destinationIndex >= motherNatureIndex) ?
                destinationIndex - motherNatureIndex :
                archipelagos.size() - (motherNatureIndex - destinationIndex);
    }
}
