package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.views.player_view.CharactersRenderer;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.LightSchoolBoard;
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.List;

public class PlayerViewRenderer {
    public static Pane renderGameState(LightGameState lgs) {
        Pane pane = new GridPane();

        var mySchoolBoardId = lgs.usernameToSchoolBoardId.get(GUI.gameClient.username);
        LightSchoolBoard mySchoolBoard = null;
        for(var schoolBoard : lgs.schoolBoards) {
            if(schoolBoard.id == mySchoolBoardId)
                mySchoolBoard = schoolBoard;
        }

        List<Pane> characters = null;
        var schoolBoard = SchoolBoardRenderer.renderSchoolBoard(mySchoolBoard);
        var archipelagos = ArchipelagosRenderer.renderArchipelagos(lgs.archipelagos, lgs.motherNaturePosition);
        var clouds = CloudsRenderer.renderClouds(lgs.clouds);
        var cards = CardsRenderer.renderCards(mySchoolBoard.deck);
        var chat = ChatRenderer.renderChat();
        var switchButton = SwitchButtonRenderer.renderSwitchButton("Enemies view", Layout.switchButtonToEnemyRect, GUI.View.EnemiesView);
        var endTurnButton = ButtonsRenderer.renderEndTurnButton();
        var undoButton = ButtonsRenderer.renderUndoButton();
        if(lgs.availableCharacters != null) characters = CharactersRenderer.renderCharacters(lgs);

        GUI.rectangleToComponent.put(Layout.schoolRect, schoolBoard);
        GUI.rectangleToComponent.put(Layout.archRect, archipelagos);
        GUI.rectangleToComponent.put(Layout.cloudRect, clouds);
        GUI.rectangleToComponent.put(Layout.cardsRect, cards);
        GUI.rectangleToComponent.put(Layout.chatRect, chat);
        GUI.rectangleToComponent.put(Layout.switchButtonToEnemyRect, switchButton);
        GUI.rectangleToComponent.put(Layout.endTurnButtonRect, endTurnButton);
        GUI.rectangleToComponent.put(Layout.undoButtonRect, undoButton);
        if(characters != null) {
            GUI.rectangleToComponent.put(Layout.firstCharacterRect, characters.get(0));
            GUI.rectangleToComponent.put(Layout.firstCharacterRect.sameToTheRight(), characters.get(1));
            GUI.rectangleToComponent.put(Layout.firstCharacterRect.sameToTheRight().sameToTheRight(), characters.get(2));
        }

        pane.getChildren().addAll(
                archipelagos,
                schoolBoard,
                clouds,
                cards,
                chat,
                switchButton,
                endTurnButton,
                undoButton
        );
        if(characters != null)
            pane.getChildren().addAll(
                characters.get(0), // first character
                characters.get(1),
                characters.get(2), // last character
                characters.get(3), // first character's prompt
                characters.get(4),
                characters.get(5)  // last character's prompt
            );

        return pane;
    }
}
