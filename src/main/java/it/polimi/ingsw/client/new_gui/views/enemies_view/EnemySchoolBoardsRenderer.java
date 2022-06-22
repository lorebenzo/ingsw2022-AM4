package it.polimi.ingsw.client.new_gui.views.enemies_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.views.player_view.SchoolBoardRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.LightSchoolBoard;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Optional;

public class EnemySchoolBoardsRenderer {
    public static int schoolBoardChosenId = 0;

    public static Pane renderChosenSchoolBoard(LightGameState lgs) {
        var pane = new Pane();

        var enemySchoolBoards = lgs.schoolBoards
                .stream()
                .filter(schoolBoard -> schoolBoard.id != lgs.currentPlayerSchoolBoardId)
                .toList();

        var chosenSchoolBoardOptional = enemySchoolBoards
                .stream()
                .filter(schoolBoard -> schoolBoard.id == schoolBoardChosenId)
                .findFirst();

        if(chosenSchoolBoardOptional.isEmpty()) {
            schoolBoardChosenId = enemySchoolBoards.get(0).id;
            pane.getChildren().add(SchoolBoardRenderer.renderSchoolBoard(enemySchoolBoards.get(0)));

            // Add last card played
            pane.getChildren().add(renderLastCardPlayed(enemySchoolBoards.get(0), Card.GOOSE)); // TODO: get last card played from LGS
        } else {
            pane.getChildren().add(SchoolBoardRenderer.renderSchoolBoard(chosenSchoolBoardOptional.get()));

            // Add last card played
            pane.getChildren().add(renderLastCardPlayed(chosenSchoolBoardOptional.get(), Card.GOOSE));
        }

        return pane;
    }

    public static Pane renderLastCardPlayed(LightSchoolBoard schoolBoard, Card lastCardPlayed) {
        var pane = new Pane();

        var cardImg = new ImageView(AssetHolder.cardImageMap.get(lastCardPlayed));
        Layout.cardPlayedRect.fitImageViewToThis(cardImg);

        pane.getChildren().add(cardImg);

        return pane;
    }
}
