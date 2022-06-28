package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.client.new_gui.user_experience.UserExperience;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.LightPlayableCharacter;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CharactersRenderer {
    private final static int studentSize = 17;

    public static List<Pane> renderCharacters(LightGameState lgs) {
        var ch1 = lgs.availableCharacters.get(0);
        var ch2 = lgs.availableCharacters.get(1);
        var ch3 = lgs.availableCharacters.get(2);

        var ch1Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch1.characterId).get()));
        var ch2Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch2.characterId).get()));
        var ch3Img = new ImageView(AssetHolder.characterToImage.get(Character.fromId(ch3.characterId).get()));

        Layout.firstCharacterRect.fitImageViewToThis(ch1Img);
        Layout.firstCharacterRect.sameToTheRight().fitImageViewToThis(ch2Img);
        Layout.firstCharacterRect.sameToTheRight().sameToTheRight().fitImageViewToThis(ch3Img);

        var characterPanes = new LinkedList<Pane>();

        characterPanes.add(new Pane(ch1Img));
        characterPanes.add(new Pane(ch2Img));
        characterPanes.add(new Pane(ch3Img));

        var prompt1 = renderPrompt(ch1, ch1Img);
        var prompt2 = renderPrompt(ch2, ch2Img);
        var prompt3 = renderPrompt(ch3, ch3Img);

        characterPanes.add(prompt1);
        characterPanes.add(prompt2);
        characterPanes.add(prompt3);



        return characterPanes;
    }

    private static Pane renderPrompt(LightPlayableCharacter lpc, ImageView character) {
        var prompt = new Pane();

        // Create controller
        var controller = new CharacterPromptController(prompt, lpc.characterId);

        prompt.setMaxWidth(GUI.SizeHandler.getX(Layout.characterPromptRelW));
        prompt.setMaxHeight(GUI.SizeHandler.getY(Layout.characterPromptRelH));

        // Translate to the center
        prompt.setTranslateX(GUI.SizeHandler.getX(Layout.characterPromptRelX));

        prompt.setVisible(false);
        prompt.setStyle("-fx-border-color: black; -fx-background-color: white; -fx-view-order: -2000;");

        var column = new VBox();
        column.setStyle("-fx-padding: 10");
        column.prefWidthProperty().bind(prompt.widthProperty());
        column.prefHeightProperty().bind(prompt.heightProperty());
        prompt.getChildren().add(column);

        // Render cost
        var cost = new Text("Cost " + lpc.currentCost);
        cost.setTextAlignment(TextAlignment.CENTER);
        column.getChildren().add(cost);

        // Render Text for character 7
        if(lpc.characterId == 7) {
            var studentsOnThisCard = new Text("Students on this card:");
            studentsOnThisCard.setTextAlignment(TextAlignment.CENTER);
            column.getChildren().add(studentsOnThisCard);
        }

        // Render students
        if(lpc.students != null && !lpc.students.isEmpty() || lpc.characterId == 9 || lpc.characterId == 12) {
            var students =
                    (lpc.characterId == 9 || lpc.characterId == 12) ?
                    Arrays.stream(Color.values()).toList() : // Display color picker for characters 9 and 12
                    lpc.students;


            var studentsBar = new HBox();
            studentsBar.setSpacing(1);
            studentsBar.setStyle("-fx-padding: 3");
            column.getChildren().add(studentsBar);
            for(var student : students) {
                ImageView studentImg = renderStudent(student, studentSize);
                var studentImgAsButton = buttonifyStudent(studentImg);

                studentsBar.getChildren().add(studentImgAsButton);

                controller.addStudentInCharacter(student, studentImg, studentImgAsButton);
            }
        }

        // Render students in the entrance for character 7 and 10
        if(lpc.characterId == 7 || lpc.characterId == 10) {
            var studentsInTheEntranceText = new Text("Students in the entrance:");
            studentsInTheEntranceText.setTextAlignment(TextAlignment.CENTER);
            column.getChildren().add(studentsInTheEntranceText);

            var studentsBar = new HBox();
            studentsBar.setSpacing(1);
            studentsBar.setStyle("-fx-padding: 3");
            column.getChildren().add(studentsBar);

            // Get my schoolBoard
            var schoolBoardId = GUI.gameClient.lastSnapshot.usernameToSchoolBoardId.get(GUI.gameClient.username);
            var schoolBoard = GUI.gameClient.lastSnapshot.schoolBoards
                    .stream()
                    .filter(lightSchoolBoard -> lightSchoolBoard.id == schoolBoardId)
                    .findFirst().get();

            for(var student : schoolBoard.studentsInTheEntrance) {
                var studentImg = renderStudent(student, studentSize);
                var studentImgAsButton = buttonifyStudent(studentImg);
                studentsBar.getChildren().add(studentImgAsButton);

                controller.addStudentInEntrance(student, studentImg, studentImgAsButton);
            }
        }

        // Render students in the dining room for character 10 (draw at max 2 students
        if(lpc.characterId == 10) {
            var studentsInDiningRoomText = new Text("Students in the dining room:");
            studentsInDiningRoomText.setTextAlignment(TextAlignment.CENTER);
            column.getChildren().add(studentsInDiningRoomText);

            var studentsBar = new HBox();
            studentsBar.setSpacing(1);
            studentsBar.setStyle("-fx-padding: 3");
            column.getChildren().add(studentsBar);

            // Get my schoolBoard
            var schoolBoardId = GUI.gameClient.lastSnapshot.usernameToSchoolBoardId.get(GUI.gameClient.username);
            var schoolBoard = GUI.gameClient.lastSnapshot.schoolBoards
                    .stream()
                    .filter(lightSchoolBoard -> lightSchoolBoard.id == schoolBoardId)
                    .findFirst().get();

            for(var student : Color.values()) {
                var studentsColumn = new VBox();

                for(int i = 0; i < Math.min(2, schoolBoard.diningRoomLaneColorToNumberOfStudents.get(student)); i++) {
                    var studentImg = renderStudent(student, studentSize);
                    var studentImgAsButton = buttonifyStudent(studentImg);
                    studentsColumn.getChildren().add(studentImgAsButton);

                    controller.addStudentInDining(student, studentImg, studentImgAsButton);
                }

                if(!studentsColumn.getChildren().isEmpty())
                    studentsBar.getChildren().add(studentsColumn);
            }
        }

        // Render effect
        var characterEffect = new TextArea(lpc.effect);
        characterEffect.setWrapText(true);
        characterEffect.setStyle("-fx-progress-color: grey");
        characterEffect.setEditable(false);
        characterEffect.prefWidthProperty().bind(column.prefWidthProperty());
        characterEffect.prefHeightProperty().bind(column.prefHeightProperty());
        column.getChildren().add(characterEffect);

        // Render activate effect button
        var bottomButtonBar = new ButtonBar();
        bottomButtonBar.setStyle("-fx-padding: 5");
        column.getChildren().add(bottomButtonBar);

        var activate = new Button("ACTIVATE");
        var exit = new Button("EXIT");

        bottomButtonBar.getButtons().addAll(activate, exit);

        controller.addActivateButton(activate);
        controller.addExitButton(exit);


        // Event listeners
        character.setOnMouseClicked(mouseEvent -> {
            prompt.setVisible(!prompt.isVisible());
        });

        controller.addEventListeners();

        return prompt;
    }

    private static ImageView renderStudent(Color student, int studentSize) {
        ImageView studentImg = new ImageView(AssetHolder.studentColorToStudentAsset.get(student));
        studentImg.setFitHeight(studentSize);
        studentImg.setFitWidth(studentSize);
        return studentImg;
    }

    private static Button buttonifyStudent(ImageView studentImg) {
        var studentImgAsButton = new Button("", studentImg);
        studentImgAsButton.setStyle(
                "-fx-border-color: transparent;" +
                "-fx-border-width: 0;" +
                "-fx-background-radius: 0;" +
                "-fx-background-color: transparent;" +
                "-fx-padding: 0"
        );
        return studentImgAsButton;
    }
}

class CharacterPromptController {
    public class StudentEntity {
        private final Color color;
        private final ImageView image;
        private final Button button;
        private boolean selected = false;

        public StudentEntity(Color color, ImageView image, Button button) {
            this.color = color;
            this.image = image;
            this.button = button;
        }
    }

    private final Pane prompt;
    private final int characterId;
    private List<StudentEntity> studentsInCharacters = new LinkedList();
    private List<StudentEntity> studentsInTheEntrance = new LinkedList();
    private List<StudentEntity> studentsInDining = new LinkedList<>();
    private Button activate = null;
    private Button exit = null;

    public CharacterPromptController(Pane prompt, int characterId) {
        this.prompt = prompt;
        this.characterId = characterId;
    }

    private static final double studentBrightnessDelta = 0.5;

    public void addStudentInCharacter(Color color, ImageView img, Button button) {
        this.studentsInCharacters.add(new StudentEntity(color, img, button));
    }

    public void addStudentInEntrance(Color color, ImageView img, Button button) {
        this.studentsInTheEntrance.add(new StudentEntity(color, img, button));
    }

    public void addStudentInDining(Color color, ImageView img, Button button) {
        this.studentsInDining.add(new StudentEntity(color, img, button));
    }

    public void addActivateButton(Button button) {
        this.activate = button;
    }

    public void addExitButton(Button button) {
        this.exit = button;
    }


    public void addEventListeners() {
        for(var student : studentsInCharacters)
            this.addStudentEventListeners(student);
        for(var student : studentsInTheEntrance)
            this.addStudentEventListeners(student);
        for(var student : studentsInDining)
            this.addStudentEventListeners(student);
        if(activate != null) this.addActivateEventListener();
        if(exit != null) this.addExitEventListener();
    }

    private void addExitEventListener() {
        this.exit.setOnMouseClicked(mouseEvent -> {
            this.cleanUp();
        });
    }

    private void addActivateEventListener() {
        this.activate.setOnMouseClicked(mouseEvent -> {
            // Play activation sound
            UserExperience.playSound(AssetHolder.characterActivation);

            var inputParams = new InputParams();
            // Set character id
            inputParams.id(this.characterId);
            // Set student colors
            for(var student : studentsInCharacters) {
                if(student.selected) inputParams.color(student.color);
            }
            // Set students in entrance
            for(var student : studentsInTheEntrance) {
                if(student.selected) inputParams.studentFromEntrance(student.color);
            }
            // Set students in dining
            for(var student : studentsInDining) {
                if(student.selected) inputParams.studentFromDining(student.color);
            }
            var event = new InputEvent(
                    InputEventType.CharacterActivation,
                    inputParams
            );
            // Clean up
            this.cleanUp();
            InputHandler.add(event);
        });
    }

    private void cleanUp() {
        // Clean up
        for(var student : this.studentsInCharacters) {
            // Unselect
            student.selected = false;
            // Reset brightness
            UserExperience.setImageViewBrightness(student.image, 0);
        }

        for(var student : this.studentsInTheEntrance) {
            // Unselect
            student.selected = false;
            // Reset brightness
            UserExperience.setImageViewBrightness(student.image, 0);
        }

        for(var student : this.studentsInDining) {
            // Unselect
            student.selected = false;
            // Reset brightness
            UserExperience.setImageViewBrightness(student.image, 0);
        }

        // Hide prompt
        this.prompt.setVisible(false);
    }

    private void addStudentEventListeners(StudentEntity student) {
        // Button click listener
        student.button.setOnMouseClicked(mouseEvent -> {
            // Play sound
            UserExperience.playSound(AssetHolder.mouseClickSound);

            double deltaBrightness = student.selected ? 0 : -studentBrightnessDelta;
            student.selected = !student.selected;
            UserExperience.setImageViewBrightness(student.image, deltaBrightness);
        });
    }
}