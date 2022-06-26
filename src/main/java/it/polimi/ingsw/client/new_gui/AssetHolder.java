package it.polimi.ingsw.client.new_gui;

import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.TowerColor;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class AssetHolder {
    // Images
    public static Image schoolBoardAsset;
    public static Map<Color, Image> studentColorToStudentAsset = new HashMap<>();
    public static Map<Color, Image> professorColorToProfessorAsset = new HashMap<>();
    public static Image island;
    public static Image motherNatureCard;
    public static Image motherNaturePawn;
    public static Map<TowerColor, Image> towerColorImageMap = new HashMap<>();
    public static Image cloud;
    public static Map<Card, Image> cardImageMap = new HashMap<>();
    private static Map<Character, String> characterToAssetFileNameMap = new HashMap<>();
    public static Map<Character, Image> characterToImage = new HashMap<>();
    public static Image trumpCursor;
    public static Image eryantis_background;

    // Sounds
    public static Media mouseClickSound;
    public static Media backgroundMusic;
    public static Media characterActivation;

    static {
        try {
            schoolBoardAsset = new Image(new FileInputStream("resources/assets/plancia.png"));

            // Background
            eryantis_background = new Image(new FileInputStream("resources/assets/backgrounds/eryantis_background.png"));

            // Students
            studentColorToStudentAsset.put(Color.YELLOW, new Image(new FileInputStream("resources/assets/students/student_yellow.png")));
            studentColorToStudentAsset.put(Color.CYAN, new Image(new FileInputStream("resources/assets/students/student_blue.png")));
            studentColorToStudentAsset.put(Color.GREEN, new Image(new FileInputStream("resources/assets/students/student_green.png")));
            studentColorToStudentAsset.put(Color.PURPLE, new Image(new FileInputStream("resources/assets/students/student_pink.png")));
            studentColorToStudentAsset.put(Color.RED, new Image(new FileInputStream("resources/assets/students/student_red.png")));

            // Professors
            professorColorToProfessorAsset.put(Color.YELLOW, new Image(new FileInputStream("resources/assets/professors/teacher_yellow.png")));
            professorColorToProfessorAsset.put(Color.CYAN, new Image(new FileInputStream("resources/assets/professors/teacher_blue.png")));
            professorColorToProfessorAsset.put(Color.GREEN, new Image(new FileInputStream("resources/assets/professors/teacher_green.png")));
            professorColorToProfessorAsset.put(Color.PURPLE, new Image(new FileInputStream("resources/assets/professors/teacher_pink.png")));
            professorColorToProfessorAsset.put(Color.RED, new Image(new FileInputStream("resources/assets/professors/teacher_red.png")));


            // Island
            island = new Image(new FileInputStream("resources/assets/archipelago/island1.png"));

            // Mother nature
            motherNatureCard = new Image(new FileInputStream("resources/assets/mother_nature/mother_nature.png"));
            motherNaturePawn = new Image(new FileInputStream("resources/assets/mother_nature/mothernature.png"));

            // Towers
            towerColorImageMap.put(TowerColor.GRAY, new Image(new FileInputStream("resources/assets/towers/grey_tower_preview_rev_1.png")));
            towerColorImageMap.put(TowerColor.WHITE, new Image(new FileInputStream("resources/assets/towers/white_tower_preview_rev_1.png")));
            towerColorImageMap.put(TowerColor.BLACK, new Image(new FileInputStream("resources/assets/towers/black_tower_preview_rev_1.png")));

            // Cloud
            cloud = new Image(new FileInputStream("resources/assets/clouds/cloud_preview_rev_1.png"));

            // Cards
            for(var card : Card.values())
                cardImageMap.put(card, new Image(new FileInputStream("resources/assets/cards/Assistente (" + card.getValue() + ").png")));

            // Mouse click
            mouseClickSound = new Media(new File("resources/assets/sounds/mouse_click.mp3").toURI().toString());
            // Cursors
            trumpCursor = new Image(new FileInputStream("resources/assets/cursors/peo1041.png"));

            // Background music
            backgroundMusic = new Media(new File("resources/assets/music/background_music.mp3").toURI().toString());
            // Character activation
            characterActivation = new Media(new File("resources/assets/sounds/character_activation.mp3").toURI().toString());

            // Characters
            int i = 1;
            for (Character c : Character.values())
                if(!c.equals(Character.NONE)) {
                    characterToAssetFileNameMap.put(c, "character_" + i + ".jpg");
                    i++;
                }

            for(Character c : Character.values())
                if(!c.equals(Character.NONE))
                    characterToImage.put(
                            c,
                            new Image(
                                    new FileInputStream(
                                            "resources/assets/characters/" + characterToAssetFileNameMap.get(c)
                                    )
                            )
                    );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
