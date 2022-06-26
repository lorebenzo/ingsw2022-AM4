package it.polimi.ingsw.client.new_gui.views.login_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.concurrent.atomic.AtomicReference;

public class LoginViewRenderer {
    public static Pane renderLoginView() {
        var pane = new Pane();

        var background = new Background(
                new BackgroundImage(
                        AssetHolder.eryantis_background,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        new BackgroundSize(
                                GUI.SizeHandler.realWidth,
                                GUI.SizeHandler.realHeight,
                                false,
                                false,
                                false,
                                false
                        )
                )
        );
        pane.setBackground(background);
        pane.setStyle("-fx-padding: 20;");

        // Vertical Login panel
        var loginPanel = new VBox();
        pane.getChildren().add(loginPanel);
        // Style
        loginPanel.setStyle(
            "-fx-padding: 10;" +
            "-fx-background-color: skyblue;" +
            "-fx-border-radius: 3;" +
            "-fx-border-color: grey;" +
            "-fx-border-width: 2"
        );
        loginPanel.setSpacing(20);
        loginPanel.setPrefWidth(270);
        loginPanel.layoutXProperty().bind(pane.layoutXProperty().add(20));
        loginPanel.layoutYProperty().bind(pane.layoutYProperty().add(20));

        var usernameBox = new HBox();
        usernameBox.setAlignment(Pos.CENTER);
        usernameBox.setSpacing(10);
        loginPanel.getChildren().add(usernameBox);
        Label username = new Label("Username:");
        username.setStyle("-fx-font-weight: bold");
        usernameBox.getChildren().add(username);
        TextField usernameField= new TextField();
        usernameBox.getChildren().add(usernameField);

        var passwordBox = new HBox();
        passwordBox.setAlignment(Pos.CENTER);
        passwordBox.setSpacing(10);
        loginPanel.getChildren().add(passwordBox);
        Label password = new Label("Password:");
        password.setStyle("-fx-font-weight: bold");
        passwordBox.getChildren().add(password);
        PasswordField passwordField = new PasswordField();
        passwordBox.getChildren().add(passwordField);


        var logStyle =
            "-fx-background-color: pink;" +
            "-fx-font-weight: bold";
        var signStyle =
            "-fx-background-color: pink;" +
            "-fx-font-weight: bold";

        var buttons = new ButtonBar();
        loginPanel.getChildren().add(buttons);
        Button login = new Button("Login");
        login.setStyle(logStyle);
        buttons.getButtons().add(login);
        Button signUp = new Button("Sign Up");
        buttons.getButtons().add(signUp);
        signUp.setStyle(signStyle);

        login.hoverProperty().addListener((obs, oldVal, hovering) -> {
            if(hovering) {
                login.setStyle(
                    "-fx-background-color: purple;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: white"
                );
            } else login.setStyle(logStyle);
        });

        signUp.hoverProperty().addListener((obs, oldVal, hovering) -> {
            if(hovering) {
                signUp.setStyle(
                    "-fx-background-color: purple;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: white"
                );
            } else signUp.setStyle(signStyle);
        });

        login.setOnMouseClicked(e -> {
            GUI.gameClient.login(usernameField.getText(), passwordField.getText());
        });

        signUp.setOnMouseClicked(e -> {
            GUI.gameClient.signUp(usernameField.getText(), passwordField.getText());
        });

        return pane;
    }
}