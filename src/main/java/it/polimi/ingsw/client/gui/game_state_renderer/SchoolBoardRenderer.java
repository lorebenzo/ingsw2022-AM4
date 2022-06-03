//package it.polimi.ingsw.client.gui.game_state_renderer;
//
//import it.polimi.ingsw.client.gui.UserInputHandler;
//import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
//import it.polimi.ingsw.server.model.game_logic.enums.Card;
//import it.polimi.ingsw.server.model.game_logic.enums.Color;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//
//import java.util.List;
//import java.util.Map;
//
//public class SchoolBoardRenderer {
//    static VBox renderSchoolBoard(SchoolBoard schoolBoard) {
//        var schoolBoardPane = new VBox();
//
//        // Header
//        var textBox = new Text("Schoolboard " + schoolBoard.getId());
//        schoolBoardPane.getChildren().add(textBox);
//
//        // Dining Room
//        var diningRoom = renderDiningRoom(schoolBoard);
//        schoolBoardPane.getChildren().add(diningRoom);
//
//        // Entrance
//        var entrance = renderEntrance(schoolBoard);
//        schoolBoardPane.getChildren().add(entrance);
//
//        // Cards
//        var cards = renderCards(schoolBoard);
//        schoolBoardPane.getChildren().add(cards);
//
//        // Set style
//        schoolBoardPane.setStyle("-fx-padding: 10");
//        schoolBoardPane.setStyle("-fx-border-color: black");
//
//        return schoolBoardPane;
//    }
//
//    private static HBox renderCards(SchoolBoard schoolBoard) {
//        List<Card> deck = schoolBoard.getDeck();
//
//        var cards = new HBox();
//
//        for(var card : deck) {
//            var cardElement = new Text(GameStateRenderer.cardToUnicode.get(card) + " ");
//
//            // Add event listener
//            cardElement.setOnMouseClicked(mouseEvent -> UserInputHandler.onCardClick(card));
//            cardElement.setOnMouseEntered(mouseDragEvent -> UserInputHandler.onCardHover(card));
//
//            cards.getChildren().add(cardElement);
//        }
//
//        return cards;
//    }
//
//    private static HBox renderEntrance(SchoolBoard schoolBoard) {
//        List<Color> studentsInTheEntrance = schoolBoard.getStudentsInTheEntrance();
//
//        var entrance = new HBox();
//
//        entrance.getChildren().add(new Text("Entrance: "));
//
//        for(var student : studentsInTheEntrance) {
//            var text = new Text(GameStateRenderer.studentSymbol);
//            text.setFill(GameStateRenderer.colorMap.get(student));
//
//            // Add event listener
//            text.setOnMouseClicked(mouseEvent -> UserInputHandler.onStudentClickEntrance(student, schoolBoard.getId()));
//
//            entrance.getChildren().add(text);
//        }
//
//        return entrance;
//    }
//
//    private static VBox renderDiningRoom(SchoolBoard schoolBoard) {
//        Map<Color, Integer> diningRoomLaneToNumberOfStudents = schoolBoard.getDiningRoomLaneColorToNumberOfStudents();
//
//        var diningRoom = new VBox();
//
//        for(var color : Color.values()) {
//            // Text string
//            StringBuilder text = new StringBuilder();
//
//            // Lane representation
//            var lane = new Text();
//
//            // Set lane symbol
//            String symbol = "⬤";
//            text.append(symbol).append("  ");
//
//            // Add students
//            for(int i = 0; i < diningRoomLaneToNumberOfStudents.get(color); i++)
//                text.append(GameStateRenderer.studentSymbol + " ");
//
//            // Initialize lane
//            lane.setText(text.toString());
//
//            // Set color
//            lane.setFill(GameStateRenderer.colorMap.get(color));
//
//            // Add lane to the dining room
//            diningRoom.getChildren().add(lane);
//        }
//
//        return diningRoom;
//    }
//}
