package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

public class CardsRenderer {
    public static Pane renderCards(List<Card> cards) {
        var pane = new Pane();

        int displayedCards = 0;
        for(Card card : cards) {
            var cardImgView = new ImageView(AssetHolder.cardImageMap.get(card));
            var cardRect = Layout.cardsRect.relativeToThis(
                    displayedCards * 10, 0, 10, 100
            );
            var magnifyEffect = 1.8;
            var increasedCardRect = new GUI.Rectangle(
                    cardRect.relX,
                    cardRect.relY - cardRect.relH / magnifyEffect,
                    cardRect.relW * magnifyEffect,
                    cardRect.relH * magnifyEffect
            );
            cardRect.fitImageViewToThis(cardImgView);

            // Event listeners
            cardImgView.setOnMouseClicked(event -> onMouseClickCard(cardImgView, card));
            cardImgView.hoverProperty().addListener((obs, oldVal, hovering) -> {
                if(hovering) {
                    // Magnify and set to foreground
                    increasedCardRect.fitImageViewToThis(cardImgView);
                    cardImgView.setStyle("-fx-view-order:-1001");
                } else {
                    // Set to normal size and background
                    cardRect.fitImageViewToThis(cardImgView);
                    cardImgView.setStyle("-fx-view-order:1");
                }
            });

            pane.getChildren().add(cardImgView);
            displayedCards++;
        }

        return pane;
    }

    private static void onMouseClickCard(ImageView cardImgView, Card card) {
        // Down-Up effect
        UserExperience.doDownUpEffect(cardImgView, 3, 100);

        // Mouse click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Generate Event
        InputHandler.add(new InputEvent(
                InputEventType.CardClick,
                new InputParams().card(card)
        ));
    }
}