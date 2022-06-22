package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

public class CloudsRenderer {
    public static Pane renderClouds(List<List<Color>> clouds) {
        var pane = new Pane();

        for(int i = 0; i < clouds.size(); i++) {
            var cloudRect = Layout.cloudRect.squareFromWidthRelativeToThis(
                    25 * i,
                    0,
                    25
            );
            var cloudImgView = new ImageView(AssetHolder.cloud);

            // Apply darkening effect
            UserExperience.setImageViewBrightness(cloudImgView, Layout.cloudDefaultBrightness);

            cloudRect.fitImageViewToThis(cloudImgView);

            // Render cloud prompt
            var prompt = PromptRenderer.renderCloudPrompt(clouds.get(i));
            prompt.setVisible(false);
            pane.getChildren().add(prompt);

            // Event listeners

            // Click listener
            int cloudIndex = i;
            cloudImgView.setOnMouseClicked(event -> onMouseClickCloud(cloudImgView, cloudIndex));

            // Hover listener
            cloudImgView.hoverProperty().addListener((obs, oldVal, hovering) -> {
                // Display prompt only if mouse is hovering on cloud
                prompt.setVisible(hovering);

                // Bright effect on hover
                if(hovering) {
                    UserExperience.setImageViewBrightness(cloudImgView, Layout.cloudDefaultBrightness + 0.1);
                } else {
                    UserExperience.setImageViewBrightness(cloudImgView, Layout.cloudDefaultBrightness);
                }
            });

            pane.getChildren().add(cloudImgView);
        }

        return pane;
    }

    private static void onMouseClickCloud(ImageView cloudImgView, int cloudIndex) {
        // Down-Up effect
        UserExperience.doDownUpEffect(cloudImgView, 3, 100);

        // Mouse click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Generate event
        InputHandler.add(new InputEvent(
                InputEventType.CloudClick,
                new InputParams().id(cloudIndex)
        ));
    }
}
