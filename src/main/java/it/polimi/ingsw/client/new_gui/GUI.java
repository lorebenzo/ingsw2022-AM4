package it.polimi.ingsw.client.new_gui;

import com.google.gson.Gson;
import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.client.new_gui.views.enemies_view.EnemyViewRenderer;
import it.polimi.ingsw.client.new_gui.views.login_view.LoginViewRenderer;
import it.polimi.ingsw.client.new_gui.views.matchmaking_view.MatchMakingRenderer;
import it.polimi.ingsw.client.new_gui.views.player_view.*;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class GUI extends Application {
    private static Boolean preventResize = false;

    public static final HashMap<Rectangle, Node> rectangleToComponent = new HashMap<>();

    private static Stage stage;

    public static void alert(String s) {
        AlertNotifyRenderer.alert(s);
        GUI.render();
    }

    public static void notify(String s) {
        AlertNotifyRenderer.notify(s);
        GUI.render();
    }

    public enum View {
        LoginView, PlayerView, EnemiesView, MatchMakingView
    }
    public static View currentView = View.PlayerView;

    public static GameClient gameClient = null;

    public static void init(String[] args, GameClient gc) {
        gameClient = gc;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GUI.stage = stage;

        stage.setResizable(true);

        stage.setWidth(SizeHandler.realWidth);
        stage.setHeight(SizeHandler.realHeight);

        stage.show();

        // Set resize listeners
        ChangeListener<Number> widthResizeListener = (obs, oldVal, newVal) -> {
            if(!preventResize) {
                SizeHandler.realWidth = newVal.intValue();
                SizeHandler.realHeight = (int) (SizeHandler.realWidth / SizeHandler.formFactor);
                render();
            }
        };

        ChangeListener<Number> heightResizeListener = (obs, oldVal, newVal) -> {
            if(!preventResize) {
                SizeHandler.realHeight = newVal.intValue();
                SizeHandler.realWidth = (int) (SizeHandler.realHeight * SizeHandler.formFactor);
                render();
            }
        };

        stage.widthProperty().addListener(widthResizeListener);
        stage.heightProperty().addListener(heightResizeListener);

        // Fetch and set icon and title
        this.setIconAndTitle(stage);

        // Start music
        UserExperience.playSoundLoop(AssetHolder.backgroundMusic);

        render();
    }

    public static void render() {
        // Set correct screen size
        preventResize = true;
        stage.setWidth(SizeHandler.realWidth);
        stage.setHeight(SizeHandler.realHeight);
        preventResize = false;

        // Clear focus map
        rectangleToComponent.clear();

        // Test
        //LightGameState lgs = null;
        //try {
        //    lgs = new Gson().fromJson(Files.readString(Path.of("src/main/resources/expert.json")), LightGameState.class);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        Pane pane;
        if(currentView.equals(View.LoginView))
            pane = LoginViewRenderer.renderLoginView();
        else if(currentView.equals(View.PlayerView))
            pane = PlayerViewRenderer.renderGameState(gameClient.lastSnapshot);
        else if(currentView.equals(View.EnemiesView))
            pane = EnemyViewRenderer.renderEnemyView(gameClient.lastSnapshot);
        else if(currentView.equals(View.MatchMakingView))
            pane = MatchMakingRenderer.renderMatchMaking();
        else
            pane = new Pane();

        // Add focus listener
        pane.setOnMouseMoved(event -> setFocusInputWhen(event.getX(), event.getY()));

        var scene = new StackPane();
        scene.getChildren().add(pane);

        // Render alert or notify if present
        AlertNotifyRenderer.getAlert().ifPresent(alert -> {
            scene.getChildren().add(alert);
        });

        // Change cursor
        scene.setCursor(new ImageCursor(AssetHolder.trumpCursor));

        // Display current player's name on the title
        var currentPlayer = gameClient.lastSnapshot.usernameToSchoolBoardID
                        .keySet()
                        .stream()
                        .filter(username ->
                                gameClient.lastSnapshot.usernameToSchoolBoardID.get(username)
                                        == gameClient.lastSnapshot.currentPlayerSchoolBoardId)
                        .findFirst().get();

        String playerTurn = "";
        if(currentView.equals(View.PlayerView) || currentView.equals(View.EnemiesView))
            playerTurn = "  §  " + currentPlayer + " 's turn";
        stage.setTitle("Eryantis" + playerTurn);

        // Set scene
        stage.setScene(new Scene(scene));
    }

    public static void switchView(View view) {
        currentView = view;
        render();
    }

    private void setIconAndTitle(Stage stage) {
        File eryantis_icon = new File("src/main/resources/assets/icons/eryantis_icon.png");
        Image eryantis_icon_img = new Image(eryantis_icon.toURI().toString());
        stage.getIcons().add(eryantis_icon_img);
        stage.setTitle("Eryantis");
    }

    /**
     * Sets focus (lower z-index) to the correct item
     */
    private static void setFocusInputWhen(double mouseX, double mouseY) {
        // Find a rectangle that contains mouse pos
        var rectangle = rectangleToComponent.keySet()
                .stream()
                .filter(rect -> rect.contains(mouseX, mouseY))
                .findFirst();

        // Focus
        rectangle.ifPresent(rect -> rectangleToComponent.get(rect).setStyle("-fx-view-order:-1000"));

        // Reset z-index of everyone to 0
        if(rectangle.isPresent())
            for(var rect : rectangleToComponent.keySet()) {
                if(!rect.equals(rectangle.get())) {
                    rectangleToComponent.get(rect).setStyle("-fx-view-order:0");
                }
            }
    }

    // Sizes handling
    public static class SizeHandler {
        public static int realWidth = 1100;
        public static int realHeight = 600;
        public static double formFactor = (float) realWidth / (float) realHeight;
        public static int barHeight = 40;

        public static int getX(double xPercentage) {
            return (int) (xPercentage / 100 * realWidth);
        }

        public static int getY(double yPercentage) {
            return (int) (yPercentage / 100 * (realHeight - barHeight));
        }
    }

    // Rectangle class
    public static class Rectangle {
        public final double relX;
        public final double relY;
        public final double relW;
        public final double relH;

        public Rectangle(double relX, double relY, double relW, double relH) {
            this.relX = relX;
            this.relY = relY;
            this.relW = relW;
            this.relH = relH;
        }

        public boolean contains(double absX, double absY) {
            return
                    SizeHandler.getX(this.relX) <= absX &&
                    SizeHandler.getX(this.relX + this.relW) >= absX &&
                    SizeHandler.getY(this.relY) <= absY &&
                    SizeHandler.getY(this.relY + this.relH) >= absY;
        }

        public Rectangle sameToTheRight() {
            return new Rectangle(
                    this.relX + this.relW, this.relY, this.relW, this.relH
            );
        }

        /**
         *
         * @param xPercentage x-position relative to the rectangle
         * @return absolute x-position
         */
        public int getX(double xPercentage) {
            return SizeHandler.getX(relX) + (int) (SizeHandler.getX(relW) * xPercentage / 100);
        }

        /**
         *
         * @param yPercentage y-position relative to the rectangle
         * @return absolute y-position
         */
        public int getY(double yPercentage) {
            return SizeHandler.getY(relY) + (int) (SizeHandler.getY(relH) * yPercentage / 100);
        }

        /**
         *
         * @return a rectangle with coordinates expressed in a system relative to this rectangle
         */
        public Rectangle relativeToThis(double _relX, double _relY, double _relW, double _relH) {
            double absX = SizeHandler.getX(this.relX) + SizeHandler.getX(this.relW) * _relX / 100.0;
            double absY = SizeHandler.getY(this.relY) + SizeHandler.getY(this.relH) * _relY / 100.0;
            double absW = SizeHandler.getX(this.relW) * _relW / 100.0;
            double absH = SizeHandler.getY(this.relH) * _relH / 100.0;
            return new Rectangle(
                absX / (double) SizeHandler.realWidth * 100.0,
                absY / (double) SizeHandler.realHeight * 100.0,
                absW / (double) SizeHandler.realWidth * 100.0,
                absH / (double) SizeHandler.realHeight * 100.0
            );
        }

        public Rectangle squareFromWidthRelativeToThis(double _relX, double _relY, double _relW) {
            double absX = SizeHandler.getX(this.relX) + SizeHandler.getX(this.relW) * _relX / 100.0;
            double absY = SizeHandler.getY(this.relY) + SizeHandler.getY(this.relH) * _relY / 100.0;
            double absW = SizeHandler.getX(this.relW) * _relW / 100.0;
            return new Rectangle(
                    absX / (double) SizeHandler.realWidth * 100.0,
                    absY / (double) SizeHandler.realHeight * 100.0,
                    absW / (double) SizeHandler.realWidth * 100.0,
                    absW / (double) SizeHandler.realHeight * 100.0
            );
        }

        public Rectangle squareFromHeightRelativeToThis(double _relX, double _relY, double _relH) {
            double absX = SizeHandler.getX(this.relX) + SizeHandler.getX(this.relW) * _relX / 100.0;
            double absY = SizeHandler.getY(this.relY) + SizeHandler.getY(this.relH) * _relY / 100.0;
            double absH = SizeHandler.getY(this.relH) * _relH / 100.0;
            return new Rectangle(
                    absX / (double) SizeHandler.realWidth * 100.0,
                    absY / (double) SizeHandler.realHeight * 100.0,
                    absH / (double) SizeHandler.realWidth * 100.0,
                    absH / (double) SizeHandler.realHeight * 100.0
            );
        }

        public void fitImageViewToThis(ImageView imgView) {
            imgView.setX(SizeHandler.getX(this.relX));
            imgView.setY(SizeHandler.getY(this.relY));
            imgView.setFitWidth(SizeHandler.getX(this.relW));
            imgView.setFitHeight(SizeHandler.getY(this.relH));
        }

        public javafx.scene.shape.Rectangle toJavaFXRect() {
            return new javafx.scene.shape.Rectangle(
                    SizeHandler.getX(this.relX),
                    SizeHandler.getY(this.relY),
                    SizeHandler.getX(this.relW),
                    SizeHandler.getY(this.relH)
            );
        }

        @Override
        public String toString() {
            String s = "";
            s += "Relative coordinates: " + "[" + relX + ", " + relY + ", " + relW + ", " + relH + "]\n";
            s += "Absolute coordinates: " + "[" + SizeHandler.getX(relX) + ", " + SizeHandler.getY(relY) + ", " +
                    SizeHandler.getX(relW) + ", " + SizeHandler.getY(relH) + "]";
            return s;
        }
    }
}
