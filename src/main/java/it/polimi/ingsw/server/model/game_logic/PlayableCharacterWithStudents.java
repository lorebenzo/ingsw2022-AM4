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

    /**
     * This method returns true if the inputted students are all contained in the character, false otherwise
     * @param students is the list of students to be checked
     * @return true if the inputted students are all contained in the character, false otherwise
     */
    @Override
    public boolean containsAllStudents(List<Color> students){
        boolean allStudentsArePresent = true;

        List<Color> entranceCopy = new ArrayList<>(this.students);

        for (Color student: students) {
            if(!entranceCopy.remove(student)) allStudentsArePresent = false;
        }
        return allStudentsArePresent;
    }

    /**
     * This method removes a student from the character
     * @param student is the student that will be removed from the character
     * @return true if the student was actually present and then removed, false otherwise
     */
    @Override
    public boolean removeStudent(Color student){
        return this.students.remove(student);
    }

    /**
     * This method adds a student to the character
     * @param student is the student that will be added to the character
     */
    @Override
    public void addStudent(Color student){
        this.students.add(student);
    }

    /**
     * This method returns the initial number of students on the character
     * @return an int representing the initial number of students on the character
     */
    @Override
    public int getInitialStudentsNumberOnCharacter() {
        return this.initialStudentsNumberOnCharacter;
    }

    /**
     * This method returns the list of the students that are present on the character
     * @return a list of the students that are present on the character
     */
    @Override
    public List<Color> getStudents() {
        return new ArrayList<>(students);
    }

    /**
     * This method returns the light version of the PlayableCharacter, containing all the useful information that need to be sent
     * over the network
     * @return a LightPlayableCharacter containing all the useful information that need to be sent over the network
     */
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