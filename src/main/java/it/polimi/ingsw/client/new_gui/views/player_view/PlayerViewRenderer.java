package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.LightSchoolBoard;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class PlayerViewRenderer {
    public static Pane renderGameState(LightGameState lgs) {
        Pane pane = new GridPane();

        LightSchoolBoard currentPlayerSchoolBoard = null;
        for(var sb : lgs.schoolBoards)
            if(sb.id == lgs.currentPlayerSchoolBoardId) {
                currentPlayerSchoolBoard = sb;
                break;
            }

        var schoolBoard = SchoolBoardRenderer.renderSchoolBoard(currentPlayerSchoolBoard);
        var archipelagos = ArchipelagosRenderer.renderArchipelagos(lgs.archipelagos, lgs.motherNaturePosition);
        var clouds = CloudsRenderer.renderClouds(lgs.clouds);
        var cards = CardsRenderer.renderCards(lgs.schoolBoards.get(0).deck);
        var chat = ChatRenderer.renderChat();
        var switchButton = SwitchButtonRenderer.renderSwitchButton("Enemies view", Layout.switchButtonToEnemyRect, GUI.View.EnemiesView);

        GUI.rectangleToComponent.put(Layout.schoolRect, schoolBoard);
        GUI.rectangleToComponent.put(Layout.archRect, archipelagos);
        GUI.rectangleToComponent.put(Layout.cloudRect, clouds);
        GUI.rectangleToComponent.put(Layout.cardsRect, cards);
        GUI.rectangleToComponent.put(Layout.chatRect, chat);
        GUI.rectangleToComponent.put(Layout.switchButtonToEnemyRect, switchButton);

        pane.getChildren().addAll(archipelagos, schoolBoard, clouds, cards, chat, switchButton);

        return pane;
    }
}
