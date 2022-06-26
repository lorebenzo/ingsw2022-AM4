package it.polimi.ingsw.client.new_gui.views.matchmaking_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public class MatchMakingRenderer {
    public static Pane renderMatchMaking() {
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
        var matchMakingPanel = new VBox();
        pane.getChildren().add(matchMakingPanel);
        // Style
        matchMakingPanel.setStyle(
                "-fx-padding: 10;" +
                        "-fx-background-color: skyblue;" +
                        "-fx-border-radius: 3;" +
                        "-fx-border-color: grey;" +
                        "-fx-border-width: 2"
        );
        matchMakingPanel.setSpacing(20);
        matchMakingPanel.setPrefWidth(310);
        matchMakingPanel.layoutXProperty().bind(pane.layoutXProperty().add(20));
        matchMakingPanel.layoutYProperty().bind(pane.layoutYProperty().add(20));

        var modeBox = new HBox();
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setSpacing(10);
        matchMakingPanel.getChildren().add(modeBox);
        Label gameMode = new Label("Game Mode: ");
        gameMode.setStyle("-fx-font-weight: bold");
        modeBox.getChildren().add(gameMode);
        Text mode = new Text("Normal");
        modeBox.getChildren().add(mode);

        // Scroll
        mode.setOnMouseClicked(e -> {
            if(mode.getText().equals("Normal")) mode.setText("Expert");
            else if(mode.getText().equals("Expert")) mode.setText("Normal");
        });

        var numberOfPlayersBox = new HBox();
        numberOfPlayersBox.setAlignment(Pos.CENTER);
        numberOfPlayersBox.setSpacing(10);
        matchMakingPanel.getChildren().add(numberOfPlayersBox);
        Label numPlayers = new Label("Number of Players: ");
        numPlayers.setStyle("-fx-font-weight: bold");
        numberOfPlayersBox.getChildren().add(numPlayers);
        Text nPLayers = new Text(Integer.toString(2));
        numberOfPlayersBox.getChildren().add(nPLayers);

        nPLayers.setOnMouseClicked(e -> {
            int n = Integer.parseInt(nPLayers.getText());
            n = n + 1 > 4 ? 2 : n + 1;
            nPLayers.setText(Integer.toString(n));
        });


        var logStyle =
                "-fx-background-color: pink;" +
                        "-fx-font-weight: bold";
        var signStyle =
                "-fx-background-color: pink;" +
                        "-fx-font-weight: bold";

        var buttons = new ButtonBar();
        matchMakingPanel.getChildren().add(buttons);
        Button joinMatchMaking = new Button("Join MatchMaking");
        joinMatchMaking.setStyle(logStyle);
        buttons.getButtons().add(joinMatchMaking);
        Button reJoin = new Button("Rejoin Previous Match");
        buttons.getButtons().add(reJoin);
        reJoin.setStyle(signStyle);

        joinMatchMaking.hoverProperty().addListener((obs, oldVal, hovering) -> {
            if(hovering) {
                joinMatchMaking.setStyle(
                        "-fx-background-color: purple;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: white"
                );
            } else joinMatchMaking.setStyle(logStyle);
        });

        reJoin.hoverProperty().addListener((obs, oldVal, hovering) -> {
            if(hovering) {
                reJoin.setStyle(
                        "-fx-background-color: purple;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: white"
                );
            } else reJoin.setStyle(signStyle);
        });

        joinMatchMaking.setOnMouseClicked(e -> {
            GUI.gameClient.joinMatchMaking(Integer.parseInt(nPLayers.getText()), mode.getText().equals("Expert"));
        });

        reJoin.setOnMouseClicked(e -> {
            GUI.gameClient.rejoinMatch();
        });

        return pane;
    }
}
