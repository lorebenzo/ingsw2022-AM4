package it.polimi.ingsw.client.new_gui;

import it.polimi.ingsw.client.new_gui.layout.Layout;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.Optional;

public class AlertNotifyRenderer {
    private static Optional<Pane> alert = Optional.empty();

    public static void alert(String alertMsg) {
        alert = Optional.of(renderAlert(alertMsg));
    }

    public static Optional<Pane> getAlert() {
        return alert;
    }

    private static Pane renderAlert(String s) {
        var pane = new Pane();

        pane.setStyle(
                "-fx-border-width: 4;" +
                "-fx-padding: 5;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 20;" +
                "-fx-background-color: white"
        );
        pane.setLayoutX(GUI.SizeHandler.realWidth / 2.0 - pane.getMaxWidth());
        pane.setMaxWidth(GUI.SizeHandler.getX(Layout.alertPromptRelW));
        pane.setMaxHeight(GUI.SizeHandler.getY(Layout.alertPromptRelH));

        var text = new TextArea("ALERT:\n" + s);
        text.setWrapText(true);
        text.setStyle("-fx-padding: 20; -fx-text-fill: red");
        text.setEditable(false);
        text.prefWidthProperty().bind(pane.widthProperty());
        text.prefHeightProperty().bind(pane.heightProperty());

        pane.getChildren().add(text);

        text.setOnMouseClicked(mouseEvent -> {
            alert = Optional.empty();
            pane.setVisible(false);
        });

        return pane;
    }
}
