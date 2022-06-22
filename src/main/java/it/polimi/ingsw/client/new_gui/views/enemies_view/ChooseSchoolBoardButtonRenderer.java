package it.polimi.ingsw.client.new_gui.views.enemies_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public class ChooseSchoolBoardButtonRenderer {
    public static Pane renderChooseSchoolBoardButton(LightGameState lgs) {
        var pane = new Pane();

        var menuButton = new MenuButton("Choose school board");
        menuButton.setLayoutX(GUI.SizeHandler.getX(Layout.chooseButtonRelX));
        menuButton.setLayoutY(GUI.SizeHandler.getY(Layout.chooseButtonRelY));
        menuButton.setMaxWidth(GUI.SizeHandler.getX(Layout.chooseButtonRelW));
        menuButton.setMaxHeight(GUI.SizeHandler.getY(Layout.chooseButtonRelH));

        var enemySchoolBoards = lgs.schoolBoards
                .stream()
                .filter(schoolBoard -> schoolBoard.getId() != lgs.currentPlayerSchoolBoardId)
                .toList();
        for(var enemySchoolBoard : enemySchoolBoards) {
            var playerName = lgs.usernameToSchoolBoardID
                    .keySet()
                    .stream()
                    .filter(plyrName -> lgs.usernameToSchoolBoardID.get(plyrName) == enemySchoolBoard.getId())
                    .findFirst()
                    .get();
            var menuItem = new MenuItem(playerName);
            menuButton.getItems().add(menuItem);

            // Event Listener
            menuItem.setOnAction(actionEvent -> {
                // Mouse click sound
                UserExperience.playSound(AssetHolder.mouseClickSound);

                EnemySchoolBoardsRenderer.schoolBoardChosenId = enemySchoolBoard.getId();
                GUI.render();
            });
        }

        // Event listener
        menuButton.setOnMouseClicked(mouseEvent -> {
            // Mouse click sound
            UserExperience.playSound(AssetHolder.mouseClickSound);
        });

        pane.getChildren().add(menuButton);
        return pane;
    }
}
