package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.utils.Randomizer;

import java.util.*;
import java.util.stream.IntStream;

public class StudentFactory {
    protected final Map<Color, Integer> studentSupply;


    public StudentFactory() {
        this.studentSupply = new HashMap<>();
        for(Color color : Color.values())
            studentSupply.put(color, GameConstants.INITIAL_STUDENTS_PER_COLOR.value);
    }

    /**
     * @throws EmptyStudentSupplyException if the student supply (representing the bag) is empty.
     * @return a random student drawn from the supply, the student is finally removed from the supply.
     */
    public Color getStudent() throws EmptyStudentSupplyException {
        int supplySize = studentSupply.keySet().stream()
                .mapToInt(studentSupply::get)
                .sum();
        if(supplySize == 0) throw new EmptyStudentSupplyException();

        List<Color> students = new ArrayList<>(supplySize);
        studentSupply.keySet().forEach(color -> {
            for(int i = 0; i < studentSupply.get(color); i++) students.add(color);
        });

        Color chosen = students.get(Randomizer.nextInt(students.size()));
        studentSupply.put(chosen, studentSupply.get(chosen) - 1);
        return chosen;
    }

    /**
     * This method returns the requested student color from the student supply and updates it accordingly
     * @param student is the student to be drawn from the supply
     * @return the student drawn from the supply
     * @throws EmptyStudentSupplyException if the student supply doesn't contain any student of the inputted color
     */
    public Color getStudent(Color student) throws EmptyStudentSupplyException {
        if(studentSupply.get(student) <= 0) throw new EmptyStudentSupplyException();

        studentSupply.put(student, studentSupply.get(student) - 1);
        return student;
    }

    /**
     * @return a random student, each color is equally likely to spawn, this method DOES NOT update the studentSupply
     */
    public Color generateStudent() {
        return Color.values()[Randomizer.nextInt(Color.values().length)];
    }

    /**
     * @throws IllegalArgumentException if(n < 0)
     * @throws EmptyStudentSupplyException if the student supply (representing the bag) is empty.
     * @param n number of students to extract from the supply
     * @return a list containing the n students that are randomly extracted from the supply
     */
    public List<Color> getNStudents(int n) throws EmptyStudentSupplyException {
        if(n < 0) throw new IllegalArgumentException();
        int supplySize = studentSupply.keySet().stream()
                .mapToInt(studentSupply::get)
                .sum();
        if(supplySize < n) throw new EmptyStudentSupplyException();
        List<Color> result = new ArrayList<>(n);
        for(int i = 0; i < n; i++)
            result.add(this.getStudent());
        return result;
    }

    /**
     * This method returns true if the student supply is empty
     * @return true if the supply is empty, false otherwise
     */
    public boolean isEmpty() {

        return this.numberOfStudentsLeft() == 0;
    }

    /**
     * This method returns the total number of students left in the supply
     * @return an int representing the number of students left in the supply
     */
    public int numberOfStudentsLeft(){
        return this.studentSupply.values().stream().mapToInt(i -> i).sum();
    }
}
