package it.polimi.ingsw.client.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.client.gui.game_state_renderer.GameStateRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.exceptions.GameStateInitializationFailureException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage(Parent parent) {
        return (Stage) parent.getScene().getWindow();
    }

    @Override
    public void start(Stage stage) throws GameStateInitializationFailureException {
        // Fetch and set icon and title
        File eryantis_icon = new File("src/main/resources/assets/icons/eryantis_icon.png");
        Image eryantis_icon_img = new Image(eryantis_icon.toURI().toString());
        stage.getIcons().add(eryantis_icon_img);
        stage.setTitle("Eryantis");

        // Create a stage renderer
        var stageRenderer = new StageRenderer(stage);

        // Test
        LightGameState gs = null;
        try {
            gs = new Gson().fromJson(Files.readString(Path.of("filename.json"), StandardCharsets.US_ASCII), LightGameState.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stageRenderer.updateGameState(gs);


        // Set starting window size
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);

        // Render scene
        stageRenderer.render();

        stage.show();
    }

    public static int getSceneWidth(Stage stage) {
        return (int) stage.getScene().getWidth();
    }

    public static int getSceneHeight(Stage stage) {
        return (int) stage.getScene().getWidth();
    }


    // Size
    public static int windowWidth = 1000;
    public static int windowHeight = 600;
}
