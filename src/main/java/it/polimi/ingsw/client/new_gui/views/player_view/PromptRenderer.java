package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.AssetHolder;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.LightArchipelago;
import it.polimi.ingsw.server.model.game_logic.LightPlayableCharacter;
import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Integers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PromptRenderer {
    /************************ Archipelago prompt *****************************/
    public static Pane renderArchipelagoPrompt(LightArchipelago archipelago) {
        var pane = new Pane();

        // Render students
        var students = new LinkedList<Color>();
        for(var key : archipelago.studentToNumber.keySet())
            for(int i = 0; i < archipelago.studentToNumber.get(key); i++)
                students.add(key);
        renderStudentsIntoPrompt(students, pane);

        // Render size of archipelago
        var sizeTextRect = Layout.promptRect.relativeToThis(
                60,
                0,
                40,
                40
        );
        var sizeTextJavaFXRect = sizeTextRect.toJavaFXRect();
        var sizeText = new Text("Size " + archipelago.islandCodes.size());
        var sizePane = new StackPane();
        sizePane.getChildren().add(sizeText);
        sizePane.setLayoutX(sizeTextJavaFXRect.getX());
        sizePane.setLayoutY(sizeTextJavaFXRect.getY());
        sizePane.setMaxWidth(sizeTextJavaFXRect.getWidth());
        sizePane.setMaxHeight(sizeTextJavaFXRect.getHeight());
        pane.getChildren().add(sizePane);

        // Render towers if they are present
        if(!archipelago.towerColor.equals(TowerColor.NONE)) {
            for(int i = 0; i < 3 /* safety */ && i < archipelago.islandCodes.size(); i++) {
                var towerRect = Layout.promptRect.relativeToThis(
                        60 + i * 13.33, 40, 13.33, 22
                );
                var towerImgView = new ImageView(AssetHolder.towerColorImageMap.get(archipelago.towerColor));
                towerRect.fitImageViewToThis(towerImgView);
                pane.getChildren().add(towerImgView);
            }
        }

        return pane;
    }

    private static void renderStudentsIntoPrompt(List<Color> students, Pane promptPane) {
        // Create student to number map
        var studentToNumber = new HashMap<Color, Integer>();
        for(var student : students) {
            studentToNumber.merge(student, 1, Integer::sum);
        }

        int studentsDisplayed = 0;
        for(int i = 0; i < Color.values().length; i++) {
            var color = Color.values()[i];
            if(studentToNumber.get(color) != null) {
                int rowOffset = 3; // (%offset)

                // Render student
                var studentRect = Layout.promptRect.squareFromHeightRelativeToThis(
                        0, studentsDisplayed * (100.0 / Color.values().length + rowOffset), 100.0 / Color.values().length
                );
                var img = new ImageView(AssetHolder.studentColorToStudentAsset.get(color));
                studentRect.fitImageViewToThis(img);
                promptPane.getChildren().add(img);

                // Render number of students
                var numberRect = Layout.promptRect.relativeToThis(
                        studentRect.relX + studentRect.relW,
                        studentsDisplayed * (100.0 / Color.values().length + rowOffset),
                        5,
                        studentRect.relH
                );
                var numberJavaFXRect = numberRect.toJavaFXRect();
                var text = new Text("x " + (studentToNumber.get(color) == null ? 0 : studentToNumber.get(color)));
                var numberPane = new StackPane();
                numberPane.getChildren().add(text);
                numberPane.setLayoutX(numberJavaFXRect.getX());
                numberPane.setLayoutY(numberJavaFXRect.getY());
                numberPane.setMaxWidth(numberJavaFXRect.getWidth());
                numberPane.setMaxHeight(numberJavaFXRect.getHeight());
                promptPane.getChildren().add(numberPane);

                studentsDisplayed++;
            }
        }
    }


    /******************* Cloud Prompt ***********************/
    public static Pane renderCloudPrompt(List<Color> cloud) {
        var pane = new Pane();

        renderStudentsIntoPrompt(cloud, pane);

        return pane;
    }


    /********************************************************/


    /******************** Characters Prompt *******************/
    public static Pane renderCharacterPrompt(LightPlayableCharacter character) {
        var prompt = new Pane();

        if(character.students != null) renderStudentsIntoPrompt(character.students, prompt);

        return prompt;
    }

    /**********************************************************/
}
