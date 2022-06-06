package it.polimi.ingsw.client.new_gui.views.login_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class LoginViewRenderer {
    public static Pane renderLoginView() {
        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label username = new Label("username:");
        grid.add(username, 0, 1);

        TextField usernameField= new TextField();
        grid.add(usernameField, 1, 1);

        Label password = new Label("password:");
        grid.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button login = new Button("Login");
        grid.add(login, 0, 3);

        Button signUp = new Button("Sign Up");
        grid.add(signUp, 1, 3);

        // Add event listeners
        login.setOnMouseClicked(mouseEvent -> {
            // Click sound
            UserExperience.playSound(AssetHolder.mouseClickSound);

            // Generate event
            InputHandler.add(new InputEvent(
                    InputEventType.Login,
                    new InputParams()
                            .username(usernameField.getText())
                            .password(passwordField.getText())
            ));
        });

        signUp.setOnMouseClicked(mouseEvent -> {
            // Click sound
            UserExperience.playSound(AssetHolder.mouseClickSound);

            // Generate event
            InputHandler.add(new InputEvent(
                    InputEventType.SignUp,
                    new InputParams()
                            .username(usernameField.getText())
                            .password(passwordField.getText())
            ));
        });

        return grid;
    }
}
