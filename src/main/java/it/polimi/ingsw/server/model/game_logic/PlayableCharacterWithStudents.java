package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Character;
import it.polimi.ingsw.server.model.game_logic.enums.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayableCharacterWithStudents extends PlayableCharacter {

    private List<Color> students;
    private int initialStudentsNumberOnCharacter;

    protected PlayableCharacterWithStudents(Character character) {
        super(character);
        this.initialStudentsNumberOnCharacter = character.studentsNumberOnCharacter;
        this.students = new LinkedList<>();

    }

    @Override
    public boolean containsAllStudents(List<Color> students){
        boolean allStudentsArePresent = true;

        List<Color> entranceCopy = new ArrayList<>(this.students);

        for (Color student: students) {
            if(!entranceCopy.remove(student)) allStudentsArePresent = false;
        }
        return allStudentsArePresent;
    }

    @Override
    public boolean removeStudent(Color student){
        return this.students.remove(student);
    }

    @Override
    public void addStudent(Color student){
        this.students.add(student);
    }

    @Override
    public int getInitialStudentsNumberOnCharacter() {
        return this.initialStudentsNumberOnCharacter;
    }

    @Override
    public List<Color> getStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public LightPlayableCharacter lightify() {
        return new LightPlayableCharacter(
                super.characterId,
                super.initialCost,
                super.currentCost,
                super.effect,
                null,
                this.students,
                null
        );
    }
}