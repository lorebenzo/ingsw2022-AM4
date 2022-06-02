package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.client.gui.game_state_renderer.GameStateRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class StageRenderer {
    private final GameStateRenderer gameStateRenderer;
    private final Stage stage;

    // State
    public boolean logged = false;
    private final List<String> logs = new LinkedList<>();

    public StageRenderer(Stage stage) {
        // Create game state renderer
        this.gameStateRenderer = new GameStateRenderer();

        // Set stage
        this.stage = stage;

        // Initialize UserInputHandler
        var gameClient = new GameClient();
        new Thread(gameClient).start();
        UserInputHandler.init(gameClient, this);
    }

    public void updateGameState(LightGameState lgs) {
        gameStateRenderer.setCurrentGameState(lgs);
        this.render();
    }

    public void renderWithPopUp(Parent popUp) {
        this.renderWithPopUp(List.of(popUp));
    }

    public void renderWithPopUp(List<Parent> popUps) {
        var baseGrid = new GridPane();

        baseGrid.add(this.getContent(), 0, 0, 5, 5);

        // Add pop-ups
        int row = 0;
        for(var popUp : popUps) {
            // Style
            popUp.setStyle("-fx-alignment: center;");
            baseGrid.add(popUp, 5, row++, 1, 1);
        }

        this.stage.setScene(new Scene(baseGrid));
    }

    public void render() {
        var content = this.getContent();
        var chatBox = this.renderChatBox();

        var finalGrid = new GridPane();
        finalGrid.add(content, 0, 0, 5, 5);
        finalGrid.add(chatBox, 5, 0, 3, 5);

        finalGrid.setGridLinesVisible(true);

        var rowConstr = new RowConstraints();
        rowConstr.setVgrow(Priority.NEVER);

        finalGrid.getRowConstraints().add(rowConstr);


        this.stage.setScene(new Scene(finalGrid));
    }

    private Parent getContent() {
        if(!this.logged) {
            return this.renderLogin();
        } else {
            return gameStateRenderer.renderGameState();
        }
    }

    private Parent renderLogin() {
        // Structure
        var baseGrid = new GridPane();

        var welcome = new Text("Welcome to Eryantis!");
        baseGrid.add(welcome, 0, 0, 2, 1);

        var usernameTextField = new TextField("username");
        baseGrid.add(usernameTextField, 1, 1);

        var usernameLabel = new Label("Username: ");
        usernameLabel.setLabelFor(usernameTextField);
        baseGrid.add(usernameLabel, 0, 1);

        var passwordField = new PasswordField();
        baseGrid.add(passwordField, 1, 2);

        var passwordLabel = new Label("Password: ");
        passwordLabel.setLabelFor(passwordField);
        baseGrid.add(passwordLabel, 0, 2);

        var signUpButton = new Button("Sign Up");
        baseGrid.add(signUpButton, 0, 3);

        var logInButton = new Button("Login");
        baseGrid.add(logInButton, 1, 3);

        var startMatchMakingButton = new Button("Enter matchmaking");
        baseGrid.add(startMatchMakingButton, 2, 3);

        // Event listeners
        signUpButton.setOnMouseClicked(mouseEvent ->
                UserInputHandler.onSignUpClick(usernameTextField.getText(), passwordField.getText()));
        logInButton.setOnMouseClicked(mouseEvent ->
                UserInputHandler.onLoginClick(usernameTextField.getText(), passwordField.getText()));
        startMatchMakingButton.setOnMouseClicked(mouseEvent ->
                UserInputHandler.onStartMatchMakingClick());

        // Style
        baseGrid.setStyle("-fx-alignment: center; -fx-background-color: azure");
        welcome.setStyle("-fx-alignment: center; -fx-padding: 100");

        return baseGrid;
    }

    private Parent renderChatBox() {
        var gridPane = new GridPane();

        var scrollPane = new ScrollPane();
        var chatBox = new VBox();

        scrollPane.setContent(chatBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        for(var log : this.logs) {
            var HBox = new HBox();
            HBox.setStyle("");

            var text = new Text(log);
            HBox.getChildren().add(text);

            chatBox.getChildren().add(HBox);
        }

        gridPane.add(scrollPane, 0, 0);

        var input = new TextArea();
        input.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                this.log(input.getText().strip());
                input.setText("");
            }
        });

        gridPane.add(input, 0, 1);

        return gridPane;
    }

    public void log(String log) {
        this.logs.add(log);
        this.render();
    }
}

