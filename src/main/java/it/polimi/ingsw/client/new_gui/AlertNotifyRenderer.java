package it.polimi.ingsw.client.new_gui;

import it.polimi.ingsw.client.new_gui.layout.Layout;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Optional;

public class AlertNotifyRenderer {
    private static Optional<Parent> alert = Optional.empty();
    private static Optional<Parent> notify = Optional.empty();

    public static void alert(String alertMsg) {
        alert = Optional.of(renderAlert(alertMsg));
    }
    public static void notify(String notifyMsg) {
        notify = Optional.of(renderNotify(notifyMsg));
    }

    public static Optional<Parent> getAlert() {
        return alert.isPresent() ? alert : notify;
    }

    private static Parent renderAlert(String s) {
        return renderMessageBox(s, "OOPS! Something went wrong :(");
    }

    private static Parent renderNotify(String s) {
        return renderMessageBox(s, "Update");
    }

    private static Parent renderMessageBox(String s, String header) {
        var pane = new VBox();

        pane.setAlignment(Pos.CENTER);
        pane.setMaxWidth(0/*GUI.SizeHandler.getX(Layout.alertPromptRelW*/);
        pane.setMaxHeight(0/*GUI.SizeHandler.getY(Layout.alertPromptRelH)*/);

        pane.setStyle(
            "-fx-background-color: skyblue;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 3;" +
            "-fx-border-color: grey;" +
            "-fx-padding: 5;"
        );

        var vBox = new VBox();
        pane.getChildren().add(vBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);


        var text = new Text(header + "\n\n" + s);
        text.setStyle(
            "-fx-font-weight: bold;" +
            "-fx-wrap-text: true;" +
            "-fx-padding: 20;" +
            "-fx-vgap: 20"
        );
        text.setWrappingWidth(200);
        vBox.getChildren().add(text);

        var buttonBar = new ButtonBar();
        var button = new Button("OK");
        var buttonStyle =
                "-fx-background-color: pink;" +
                "-fx-font-weight: bold";
        button.setStyle(buttonStyle);
        buttonBar.getButtons().add(button);
        vBox.getChildren().add(buttonBar);

        button.hoverProperty().addListener((obs, oldVal, hovering) -> {
            if(hovering) {
                button.setStyle(
                    "-fx-background-color: purple;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;"
                );
            } else button.setStyle(buttonStyle);
        });

        button.setOnMouseClicked(mouseEvent -> {
            alert = Optional.empty();
            notify = Optional.empty();
            pane.setVisible(false);
        });

        return pane;
    }
}

/*
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
 */
