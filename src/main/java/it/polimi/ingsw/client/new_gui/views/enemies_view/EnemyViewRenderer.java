package it.polimi.ingsw.client.new_gui.views.enemies_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.views.player_view.SwitchButtonRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import javafx.scene.layout.Pane;

public class EnemyViewRenderer {
    public static Pane renderEnemyView(LightGameState lgs) {
        var pane = new Pane();

        var enemyChosenSchoolBoard = EnemySchoolBoardsRenderer.renderChosenSchoolBoard(lgs);
        var chooseEnemySchoolBoardButton = ChooseSchoolBoardButtonRenderer.renderChooseSchoolBoardButton(lgs);
        var switchViewButton = SwitchButtonRenderer.renderSwitchButton("Player view", Layout.switchButtonToPlayerRect, GUI.View.PlayerView);

        GUI.rectangleToComponent.put(Layout.schoolRect, enemyChosenSchoolBoard);
        GUI.rectangleToComponent.put(Layout.chooseButtonRect, chooseEnemySchoolBoardButton);
        GUI.rectangleToComponent.put(Layout.switchButtonToEnemyRect, switchViewButton);

        pane.getChildren().addAll(enemyChosenSchoolBoard, chooseEnemySchoolBoardButton, switchViewButton);

        return pane;
    }
}
