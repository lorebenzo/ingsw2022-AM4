package it.polimi.ingsw.client.new_gui.views.enemies_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.client.new_gui.views.player_view.PromptRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.LightPlayableCharacter;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class CharactersRenderer {
    public static List<Pane> renderCharacters(LightGameState lgs) {
        var ch1 = lgs.availableCharacters.get(0);
        var ch2 = lgs.availableCharacters.get(1);
        var ch3 = lgs.availableCharacters.get(2);

        var ch1Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch1.characterId).get()));
        var ch2Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch2.characterId).get()));
        var ch3Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch3.characterId).get()));

        Layout.firstCharacterRect.fitImageViewToThis(ch1Img);
        Layout.firstCharacterRect.sameToTheRight().fitImageViewToThis(ch2Img);
        Layout.firstCharacterRect.sameToTheRight().sameToTheRight().fitImageViewToThis(ch3Img);

        setOnMouseClickedOnCharacter(ch1Img, ch1);
        setOnMouseClickedOnCharacter(ch2Img, ch2);
        setOnMouseClickedOnCharacter(ch3Img, ch3);

        var characterPanes = new LinkedList<Pane>();

        characterPanes.add(new Pane(ch1Img));
        characterPanes.add(new Pane(ch2Img));
        characterPanes.add(new Pane(ch3Img));

        var prompt1 = renderPrompt(ch1, ch1Img);
        var prompt2 = renderPrompt(ch2, ch2Img);
        var prompt3 = renderPrompt(ch3, ch3Img);

        characterPanes.add(prompt1);
        characterPanes.add(prompt2);
        characterPanes.add(prompt3);

        return characterPanes;
    }

    private static Pane renderPrompt(LightPlayableCharacter lpc, ImageView character) {
        var prompt = new Pane();

        var pane = new Pane();
        pane.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3");
        pane.setMinWidth(GUI.SizeHandler.getX(5));
        pane.setMinHeight(GUI.SizeHandler.getY(10));
        var paneRect = new GUI.Rectangle(0,0,0,0);

        // Create a vBox
        var vBox = new VBox();
        vBox.setStyle("-fx-padding: 5");

        // Render cost
        var cost = new Text("Cost: " + lpc.currentCost);
        cost.setStyle("-fx-padding: 5; -fx-text-alignment: center");
        vBox.getChildren().add(cost);

        // Render students
        if(lpc.students != null) {
            var hBox = new HBox();
            hBox.setStyle("-fx-padding: 5");
            for (var student : lpc.students) {
                var studImg = new ImageView(AssetHolder.studentColorToStudentAsset.get(student));
                var rect = new GUI.Rectangle(0,0, 3, 6.4);
                rect.fitImageViewToThis(studImg);
                hBox.getChildren().add(studImg);
            }
            vBox.getChildren().add(hBox);
        }

        // Render effect
        var effect = new Text(lpc.effect);
        effect.wrappingWidthProperty().bind(vBox.maxWidthProperty());
        vBox.getChildren().add(effect);

        pane.getChildren().add(vBox);
        prompt.getChildren().add(pane);

        prompt.setVisible(false);

        character.hoverProperty().addListener((
                (observableValue, oldValue, hovering) -> {
                    prompt.setVisible(hovering);
                }
            )
        );

        character.setOnMouseMoved(mouseEvent -> {
            int width = GUI.SizeHandler.getX(20);
            int height = GUI.SizeHandler.getY(40);
            double x = mouseEvent.getX() - width;
            double y = mouseEvent.getY() - height;

            pane.setLayoutX(x);
            pane.setLayoutY(y);
            pane.setMaxWidth(width);
            pane.setMaxHeight(height);

        });

        return prompt;
    }

    private static void setOnMouseClickedOnCharacter(ImageView imgView, LightPlayableCharacter lpc) {
        imgView.setOnMouseClicked(e -> {
            UserExperience.doDownUpEffect(imgView, 3, 100);
            UserExperience.playSound(AssetHolder.mouseClickSound);
            InputHandler.add(
                    new InputEvent(
                            InputEventType.CharacterClick,
                            new InputParams()
                                    .id(lpc.characterId)
                    )
            );
        });
    }
}
