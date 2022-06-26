package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.Coordinates;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.LightArchipelago;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.*;

public class ArchipelagosRenderer {
    private final static double archipelagosRelX = 25.0;
    private final static double archipelagosRelY = 0.0;
    private final static double archipelagosRelWidth = 55.0;
    private final static double archipelagosRelHeight = 70.0;
    private final static double correctionFactor = 0.9;
    private final static GUI.Rectangle archipelagosRectangle = new GUI.Rectangle(
            archipelagosRelX, archipelagosRelY, archipelagosRelWidth, archipelagosRelHeight
    );
    private final static double gapInRadiants = 0;

    public static Pane renderArchipelagos(List<LightArchipelago> archipelagos, int motherNatureListIndex) {
        // Render archipelagos on a circle
        var pane = new Pane();

        var circleCenterAbs =
                new Coordinates(
                        GUI.SizeHandler.getX(archipelagosRelX) + GUI.SizeHandler.getX(archipelagosRelWidth) / 2.0,
                        GUI.SizeHandler.getY(archipelagosRelY) + GUI.SizeHandler.getY(archipelagosRelHeight) / 2.0
                );

        var circleRadius = Math.min (
            0.5 * GUI.SizeHandler.getY(archipelagosRelHeight) / (1 + Math.sin(Math.PI / archipelagos.size() - gapInRadiants)),
            0.5 * GUI.SizeHandler.getX(archipelagosRelWidth) / (1 + Math.sin(Math.PI / archipelagos.size() - gapInRadiants))
        ) * correctionFactor;
        var thetaInterval = 2 * Math.PI / archipelagos.size();
        var archipelagoWidthHeight = 2 * circleRadius * Math.sin(0.5 * (thetaInterval - gapInRadiants));
        for(int i = 0; i < archipelagos.size(); i++) {
            var archipelagoCenterAbs = new Coordinates(
                    circleRadius * Math.cos(i * thetaInterval) + circleCenterAbs.x,
                    circleRadius * Math.sin(i * thetaInterval) + circleCenterAbs.y
            );

            var archipelagoPosAbs = new Coordinates(
                    archipelagoCenterAbs.x - archipelagoWidthHeight / 2,
                    archipelagoCenterAbs.y - archipelagoWidthHeight / 2
            );


            var archipelagoImageView = new ImageView(AssetHolder.island);
            archipelagoImageView.setX(archipelagoPosAbs.x);
            archipelagoImageView.setY(archipelagoPosAbs.y);
            archipelagoImageView.setFitWidth(archipelagoWidthHeight);
            archipelagoImageView.setFitHeight(archipelagoWidthHeight);

            pane.getChildren().add(archipelagoImageView);

            // Add mother nature if present
            if(i == motherNatureListIndex) {
                var motherNatureImageView = new ImageView(AssetHolder.motherNaturePawn);
                var motherNatureWidthHeight = archipelagoWidthHeight / 2;
                motherNatureImageView.setX(archipelagoCenterAbs.x - motherNatureWidthHeight / 2);
                motherNatureImageView.setY(archipelagoCenterAbs.y - motherNatureWidthHeight / 2);
                motherNatureImageView.setFitWidth(motherNatureWidthHeight);
                motherNatureImageView.setFitHeight(motherNatureWidthHeight);
                pane.getChildren().add(motherNatureImageView);

                // Event listeners

                // Click listener
                motherNatureImageView.setOnMouseClicked(e -> onMotherNatureClicked(motherNatureImageView));

                // Hover listener
                motherNatureImageView.hoverProperty().addListener((obs, oldVal, hovering) -> {
                    // Bright effect on hover
                    if(hovering) {
                        UserExperience.setImageViewBrightness(motherNatureImageView, +0.3);
                    } else {
                        UserExperience.setImageViewBrightness(motherNatureImageView, 0);
                    }
                });
            }

            // Add prompt
            var prompt = PromptRenderer.renderArchipelagoPrompt(archipelagos.get(i));
            prompt.setVisible(false);
            pane.getChildren().add(prompt);

            // Event listeners
            double archipelagoBrightness = archipelagos.get(i).lock ? -0.5 : 0;
            UserExperience.setImageViewBrightness(archipelagoImageView, archipelagoBrightness);

            // Click listener
            int archipelagoIndex = i;
            archipelagoImageView.setOnMouseClicked(e ->
                    onArchipelagoClicked(archipelagoImageView, archipelagos.get(archipelagoIndex).islandCodes.get(0)));

            // Hover listener
            int finalI = i;
            archipelagoImageView.hoverProperty().addListener((obs, oldVal, hovering) -> {
                // Display prompt only if mouse is hovering on archipelago
                prompt.setVisible(hovering);

                // Bright effect on hover
                if(!archipelagos.get(finalI).lock)
                    if(hovering) {
                        UserExperience.setImageViewBrightness(archipelagoImageView, +0.3);
                    } else {
                        UserExperience.setImageViewBrightness(archipelagoImageView, 0);
                    }
            });
        }

        return pane;
    }

    private static void onMotherNatureClicked(ImageView motherNatureImageView) {
        // Down-Up effect
        UserExperience.doDownUpEffect(motherNatureImageView, 3, 100);

        // Mouse click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Generate click event
        InputHandler.add(new InputEvent(
                InputEventType.MotherNatureClick,
                new InputParams()
        ));
    }

    private static void onArchipelagoClicked(ImageView archipelagoImgView, int archipelagoId) {
        // Down-Up effect
        UserExperience.doDownUpEffect(archipelagoImgView, 3, 100);

        // Mouse click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Generate click event
        InputHandler.add(new InputEvent(
                InputEventType.ArchipelagoClick,
                new InputParams().id(archipelagoId)
        ));
    }

}
