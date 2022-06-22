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
import it.polimi.ingsw.server.model.game_logic.SchoolBoard;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.*;

public class SchoolBoardRenderer {
    private final static double studentPercentWidthRelativeToSchoolBoard = 8.7;
    private final static double studentPercentHeightRelativeToSchoolBoard = 4.2;

    private final static double horizontalStudentPercentGap = 16.5;
    private final static double verticalStudentDiningPercentGap = 4.75;

    private final static Map<Color, Coordinates[]> studentColorToDiningLaneCoords = new HashMap<>();
    private final static Map<Color, Coordinates> studentColorToProfessorCoords = new HashMap<>();
    private final static List<Coordinates> entranceCoordinates = new LinkedList<>();

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

        // Layer 0: schoolboard
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
            var studentImgView = renderStudent(
                    student, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                    renderCoord.x, renderCoord.y
            );

            studentImgView.setOnMouseClicked(event -> onMouseClickEntrance(studentImgView, student));

            pane.getChildren().add(studentImgView);
        }

        // Layer 2: dining room students
        for(var color : Color.values()) {
            var numberOfStudents = schoolBoard.diningRoomLaneColorToNumberOfStudents.get(color);
            for(int i = 0; i < numberOfStudents; i++) {
                var coords = studentColorToDiningLaneCoords.get(color)[i];
                var studentImgView = renderStudent(
                        color, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                        coords.x, coords.y
                );

                studentImgView.setOnMouseClicked(event -> onMouseClickDiningRoom(studentImgView, color));

                pane.getChildren().add(studentImgView);
            }
        }

        // Layer 3: professors
        for(var color : Color.values()) {
            if(schoolBoard.professorsTable.contains(color)) {
                var coords = studentColorToProfessorCoords.get(color);
                var professorImgView = renderStudent(
                        color, schoolboardRect, schoolBoardImgView.getX(), schoolBoardImgView.getY(),
                        coords.x, coords.y
                );

                professorImgView.setOnMouseClicked(event -> onMouseClickProfessor(professorImgView, color));

                pane.getChildren().add(professorImgView);
            }
        }

        return pane;
    }


    private static ImageView renderStudent(
            Color color, GUI.Rectangle schoolBoardRect, double schoolBoardAbsX, double schoolBoardAbsY, double relX, double relY) {
        var student = new ImageView(AssetHolder.studentColorToStudentAsset.get(color));

        student.setX(schoolBoardAbsX + schoolBoardRect.getX(relX));
        student.setY(schoolBoardAbsY + schoolBoardRect.getY(relY));

        student.setFitWidth(schoolBoardRect.getX(studentPercentWidthRelativeToSchoolBoard));
        student.setFitHeight(schoolBoardRect.getY(studentPercentHeightRelativeToSchoolBoard));

        return student;
    }


    private static void onMouseClickSchoolBoard() {
        // Reset clicks
        InputHandler.add(new InputEvent(
                InputEventType.Reset,
                new InputParams()
        ));
    }

    private static void onMouseClickEntrance(ImageView studentImageView, Color studentColor) {
        // Down-Up effect
        UserExperience.doDownUpEffect(studentImageView, 3, 100);

        // Play click sound
        UserExperience.playSound(AssetHolder.mouseClickSound);

        // Create input event
        InputHandler.add(new InputEvent(
                InputEventType.MyStudentInEntranceClick,
                new InputParams().color(studentColor)
        ));
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
}
