package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.Coordinates;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.LightSchoolBoard;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategyFactory;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.*;

public class SchoolBoardRenderer {
    private final static double studentPercentWidthRelativeToSchoolBoard = 8.7;
    private final static double studentPercentHeightRelativeToSchoolBoard = 4.2;

    private final static double horizontalStudentPercentGap = 16.5;
    private final static double verticalStudentDiningPercentGap = 4.75;

    private final static Map<Color, Coordinates[]> studentColorToDiningLaneCoords = new HashMap<>();
    private final static Map<Color, Coordinates> studentColorToProfessorCoords = new HashMap<>();
    private final static List<Coordinates> entranceCoordinates = new LinkedList<>();

    private enum TargetType {
        Entrance, Dining, Professor
    }

    static {
        Map<Integer, Color> indexToColor = new HashMap<>();

        indexToColor.put(1, Color.GREEN);
        indexToColor.put(2, Color.RED);
        indexToColor.put(3, Color.YELLOW);
        indexToColor.put(4, Color.PURPLE);
        indexToColor.put(5, Color.CYAN);

        // Initialize studentColorToDiningLaneCoords map
        for(int i = 1; i <= 5; i++) {
            var color = indexToColor.get(i);
            studentColorToDiningLaneCoords.put(color, new Coordinates[11]);
            // Init dining room positions
            for(int j = 0; j < 10; j++) {
                studentColorToDiningLaneCoords.get(color)[j] =
                        new Coordinates(13 + (i-1) * horizontalStudentPercentGap, 77 - j * verticalStudentDiningPercentGap);
            }
        }

        // Initialize studentColorToProfessorCoords map
        for(int i = 1; i <= 5; i++) {
            var color = indexToColor.get(i);
            studentColorToProfessorCoords.put(
                    color,
                    new Coordinates(13 + (i-1) * horizontalStudentPercentGap, 25)
            );
        }

        // Initialize entranceCoordinates
        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 5; j++)
                if(!(i == 1 && j == 0)) entranceCoordinates.add(
                        new Coordinates(13 + j * horizontalStudentPercentGap, 87 + i * 6)
                );
    }

    public static Pane renderSchoolBoard(LightSchoolBoard schoolBoard) {
        var pane = new Pane();

        // Layer 0: school board
        var schoolBoardImgView = new ImageView(AssetHolder.schoolBoardAsset);
        var schoolboardRect = new GUI.Rectangle(
                Layout.schoolRelX, Layout.schoolRelY,
                Layout.schoolRelW, Layout.schoolRelH
        );
        schoolboardRect.fitImageViewToThis(schoolBoardImgView);

        // Apply darkening effect
        UserExperience.setImageViewBrightness(schoolBoardImgView, -0.3);

        pane.getChildren().add(schoolBoardImgView);

        // Add click listener
        schoolBoardImgView.setOnMouseClicked(event -> onMouseClickSchoolBoard());

        // Layer 1: entrance students
        int entranceIterationIndex = 0;
        for(var student : schoolBoard.studentsInTheEntrance) {
            var renderCoord = entranceCoordinates.get(entranceIterationIndex++);
            var studentImgView = renderStudentAndAddEventHandler(
                    student, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                    renderCoord.x, renderCoord.y, TargetType.Entrance
            );

            pane.getChildren().add(studentImgView);
        }

        // Layer 2: dining room students
        for(var color : Color.values()) {
            var numberOfStudents = schoolBoard.diningRoomLaneColorToNumberOfStudents.get(color);
            for(int i = 0; i < numberOfStudents; i++) {
                var coords = studentColorToDiningLaneCoords.get(color)[i];
                var studentImgView = renderStudentAndAddEventHandler(
                        color, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                        coords.x, coords.y, TargetType.Dining
                );

                pane.getChildren().add(studentImgView);
            }
        }

        // Layer 3: professors
        for(var color : Color.values()) {
            if(schoolBoard.professorsTable.contains(color)) {
                var coords = studentColorToProfessorCoords.get(color);
                var professorImgView = renderStudentAndAddEventHandler(
                        color, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                        coords.x, coords.y, TargetType.Professor
                );

                pane.getChildren().add(professorImgView);
            }
        }

        // Layer 4: info
        pane.getChildren().add(renderInfo(schoolBoard));

        return pane;
    }

    private static ImageView renderStudent(
            Color color, GUI.Rectangle schoolBoardRect, double schoolBoardAbsX, double schoolBoardAbsY, double relX, double relY) {
        return renderPawn(schoolBoardRect, schoolBoardAbsX, schoolBoardAbsY, relX, relY, AssetHolder.studentColorToStudentAsset.get(color));
    }

    private static ImageView renderProfessor(
            Color color, GUI.Rectangle schoolBoardRect, double schoolBoardAbsX, double schoolBoardAbsY, double relX, double relY) {
        var img = renderPawn(schoolBoardRect, schoolBoardAbsX, schoolBoardAbsY, relX, relY, AssetHolder.professorColorToProfessorAsset.get(color));
        return img;
    }


    private static ImageView renderPawn(
        GUI.Rectangle schoolBoardRect, double schoolBoardAbsX, double schoolBoardAbsY, double relX, double relY, Image asset_img
    ) {
        var asset = new ImageView(asset_img);

        asset.setX(schoolBoardAbsX + schoolBoardRect.getX(relX));
        asset.setY(schoolBoardAbsY + schoolBoardRect.getY(relY));

        asset.setFitWidth(schoolBoardRect.getX(studentPercentWidthRelativeToSchoolBoard));
        asset.setFitHeight(schoolBoardRect.getY(studentPercentHeightRelativeToSchoolBoard));

        return asset;
    }

    private static Button renderStudentAndAddEventHandler(
            Color color, GUI.Rectangle schoolBoardRect, double schoolBoardAbsX, double schoolBoardAbsY, double relX, double relY,
            TargetType targetType
    ) {
        var img = targetType.equals(TargetType.Professor) ?
                renderProfessor(color, schoolBoardRect, schoolBoardAbsX, schoolBoardAbsY, relX, relY) :
                renderStudent(color, schoolBoardRect, schoolBoardAbsX, schoolBoardAbsY, relX, relY);
        var studentImgAsButton = new Button("", img);

        studentImgAsButton.setLayoutX(img.getX());
        studentImgAsButton.setLayoutY(img.getY());

        //studentImgAsButton.layoutXProperty().bind(img.layoutXProperty());
        //studentImgAsButton.layoutYProperty().bind(img.layoutYProperty());

        studentImgAsButton.setStyle(
            "-fx-border-color: black;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 0;" +
            "-fx-background-color: transparent;" +
            "-fx-padding: 0"
        );

        switch (targetType) {
            case Entrance -> studentImgAsButton.setOnMouseClicked(event -> onMouseClickEntrance(event, img, color));
            case Dining -> studentImgAsButton.setOnMouseClicked(event -> onMouseClickDiningRoom(img, color));
            case Professor -> studentImgAsButton.setOnMouseClicked(event -> onMouseClickProfessor(img, color));
        }

        return studentImgAsButton;
    }


    private static void onMouseClickSchoolBoard() {
        // Reset clicks
        InputHandler.add(new InputEvent(
                InputEventType.Reset,
                new InputParams()
        ));
    }

    private static void onMouseClickEntrance(MouseEvent event, ImageView studentImgView, Color studentColor) {
        // Down-Up effect
        UserExperience.doDownUpEffect(studentImgView, 3, 100);

        // Play click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        if(event.getButton().equals(MouseButton.PRIMARY)) {
            // Create input event
            InputHandler.add(new InputEvent(
                    InputEventType.MyStudentInEntranceClick,
                    new InputParams().color(studentColor)
            ));
        } else {
            InputHandler.add(new InputEvent(
                    InputEventType.MyStudentInEntranceRightClick,
                    new InputParams().color(studentColor)
            ));
        }
    }

    private static void onMouseClickDiningRoom(ImageView studentImgView, Color studentColor) {
        // Down-Up effect
        UserExperience.doDownUpEffect(studentImgView, 3, 100);

        // Play click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Create input event
        InputHandler.add(new InputEvent(
                InputEventType.MyStudentInDiningRoomClick,
                new InputParams().color(studentColor)
        ));
    }

    private static void onMouseClickProfessor(ImageView professorImgView, Color professorColor) {
        // Down-Up effect
        UserExperience.doDownUpEffect(professorImgView, 3, 100);

        // Play click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Create input event
        InputHandler.add(new InputEvent(
                InputEventType.MyProfessorClick,
                new InputParams().color(professorColor)
        ));
    }

    private static VBox renderInfo(LightSchoolBoard schoolBoard) {
        // Style
        var style = "-fx-text-fill: white; -fx-font-weight: bold";

        // Coins
        var coins = new Text("Coins: " + schoolBoard.coins);
        coins.setStyle(style);

        // Tower color
        var towerColor = new Text("Tower Color: " + schoolBoard.towerColor);
        towerColor.setStyle(style);

        // Number of towers to place
        int numberOfTowers = getTowerCount(schoolBoard);

        var numOfTowers = new Text("Towers to place: " + numberOfTowers);
        numOfTowers.setStyle(style);

        var infoPane = new VBox();
        if(schoolBoard.coins != null) infoPane.getChildren().add(coins);
        infoPane.getChildren().add(towerColor);
        infoPane.getChildren().add(numOfTowers);

        var infoRect = Layout.schoolRect
                .relativeToThis(30, 10, 70, 10)
                .toJavaFXRect();
        infoPane.setLayoutX(infoRect.getX());
        infoPane.setLayoutY(infoRect.getY());
        infoPane.setMaxWidth(infoRect.getWidth());
        infoPane.setMaxHeight(infoRect.getHeight());

        return infoPane;
    }

    private static int getTowerCount(LightSchoolBoard schoolBoard) {
        var gameState = GUI.gameClient.lastSnapshot;
        int towerCount = NumberOfPlayersStrategyFactory
                .getCorrectStrategy(gameState.schoolBoards.size())
                .getNumberOfTowers();

        for(var arch : gameState.archipelagos) {
            if(arch.towerColor.equals(schoolBoard.towerColor))
                towerCount -= arch.islandCodes.size();  // Do not count towers that have already been placed
        }

        return Math.max(towerCount, 0);  // Saturate to zero
    }
}
