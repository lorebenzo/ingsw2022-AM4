package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.ArrayList;
import java.util.List;

public class CharacterWithStudents extends PlayableCharacter {

    private List<Color> studentsOnCard;

    protected CharacterWithStudents(Character character) {
        super(character);

    }

    //Card has to be initialized before being played
    public void initializeCard(List<Color> studentsOnCard){
        this.studentsOnCard = studentsOnCard;
    }

    public boolean removeStudentsFromCard(List<Color> students){
        List<Color> studentsOnCardCopy = new ArrayList<>(this.studentsOnCard);

        boolean studentWasContained = true;
        int i = 0;
        while (studentWasContained && i < students.size()) {
            studentWasContained = studentsOnCardCopy.remove(students.get(i));
        }

        if(studentWasContained){
            this.studentsOnCard = studentsOnCardCopy;
            return true;
        }
        return false;


    }
}