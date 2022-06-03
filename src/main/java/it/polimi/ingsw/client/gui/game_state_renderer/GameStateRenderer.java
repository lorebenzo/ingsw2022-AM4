//package it.polimi.ingsw.client.gui.game_state_renderer;
//
//import it.polimi.ingsw.client.gui.UserInputHandler;
//import it.polimi.ingsw.server.model.game_logic.LightGameState;
//import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
//import it.polimi.ingsw.server.model.game_logic.enums.Card;
//import it.polimi.ingsw.server.model.game_logic.enums.Color;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.effect.Light;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class GameStateRenderer {
//    private LightGameState currentGameState = null;
//
//    public static final Map<Color, javafx.scene.paint.Color> colorMap = new HashMap<>();
//    public static final Map<Card, String> cardToUnicode = new HashMap<>();
//    static {
//        colorMap.put(Color.PURPLE, javafx.scene.paint.Color.PURPLE);
//        colorMap.put(Color.YELLOW, javafx.scene.paint.Color.YELLOW);
//        colorMap.put(Color.GREEN, javafx.scene.paint.Color.GREEN);
//        colorMap.put(Color.CYAN, javafx.scene.paint.Color.CYAN);
//        colorMap.put(Color.RED, javafx.scene.paint.Color.RED);
//
//        cardToUnicode.put(Card.DOG, "🐶");
//        cardToUnicode.put(Card.GOOSE, "\uD83E\uDDA2");
//        cardToUnicode.put(Card.CAT, "\uD83D\uDC31");
//        cardToUnicode.put(Card.PARROT, "\uD83E\uDD9C");
//        cardToUnicode.put(Card.FOX, "\uD83E\uDD8A");
//        cardToUnicode.put(Card.LIZARD, "\uD83E\uDD8E");
//        cardToUnicode.put(Card.OCTOPUS, "\uD83D\uDC19");
//        cardToUnicode.put(Card.MASTIFF, "\uD83D\uDC15");
//        cardToUnicode.put(Card.ELEPHANT, "\uD83D\uDC18");
//        cardToUnicode.put(Card.TURTLE, "\uD83D\uDC22");
//    }
//
//    public static final String studentSymbol = "\uD83C\uDF93";
//
//    public GridPane renderGameState() {
//        if(this.currentGameState != null)
//            return this.renderGameState(this.currentGameState);
//        return null;
//    }
//
//    public GridPane renderGameState(LightGameState gs) {
//        this.currentGameState = gs;
//        return this.renderBaseGrid(gs);
//    }
//
//    public GridPane renderInfoPane(VBox infoPane) {
//        var baseGrid = this.renderBaseGrid(this.currentGameState);
//        baseGrid.addColumn(2, infoPane);
//        return baseGrid;
//    }
//
//    private GridPane renderBaseGrid(LightGameState gs) {
//        var baseGrid = new GridPane();
//
//        // Add event handler
//        baseGrid.setOnMouseClicked(UserInputHandler::onMouseRightClick);
//
//        // Set style
//        baseGrid.setStyle("-fx-background-color: grey; -fx-padding: 10;");
//        // baseGrid.setStyle("-fx-alignment: center");
//
//        // Render school boards
//        try {
//            var schoolBoardPane0 = SchoolBoardRenderer.renderSchoolBoard(gs.schoolBoards.get(0));
//            baseGrid.add(schoolBoardPane0, 0, 0);
//            var schoolBoardPane1 = SchoolBoardRenderer.renderSchoolBoard(gs.schoolBoards.get(1));
//            baseGrid.add(schoolBoardPane1, 1, 0);
//            var schoolBoardPane2 = SchoolBoardRenderer.renderSchoolBoard(gs.schoolBoards.get(2));
//            baseGrid.add(schoolBoardPane2, 0, 1);
//            var schoolBoardPane3 = SchoolBoardRenderer.renderSchoolBoard(gs.schoolBoards.get(3));
//            baseGrid.add(schoolBoardPane3, 1, 1);
//        } catch (IndexOutOfBoundsException ignored) { }
//
//        // Render archipelagos
//        var archipelagos = ArchipelagosRenderer.renderArchipelagos(gs.archipelagos, gs.motherNaturePosition);
//        // baseGrid.addRow(2, archipelagos);
//        baseGrid.add(archipelagos, 0, 2, 2, 1);
//        // Style
//        archipelagos.setStyle("-fx-border-color: black");
//
//        // Render clouds
//        var clouds = CloudsRenderer.renderClouds(gs.clouds);
//        // baseGrid.addRow(3, clouds);
//        baseGrid.add(clouds, 0, 3, 2, 1);
//
//        return baseGrid;
//    }
//
//    public void setCurrentGameState(LightGameState lgs) {
//        this.currentGameState = lgs;
//    }
//}
