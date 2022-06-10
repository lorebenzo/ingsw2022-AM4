package it.polimi.ingsw.server.model.game_logic;

import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import it.polimi.ingsw.server.model.game_logic.exceptions.EmptyStudentSupplyException;
import it.polimi.ingsw.server.model.game_logic.utils.Randomizer;

import java.util.*;
import java.util.stream.IntStream;

public class StudentFactory {
    // fixme: private
    public final Map<Color, Integer> studentSupply;


    public StudentFactory(long seed) {
        this.studentSupply = new HashMap<>();
        for(Color color : Color.values())
            studentSupply.put(color, GameConstants.INITIAL_STUDENTS_PER_COLOR.value);
        Randomizer.setSeed(seed);
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

        // TODO: optimize this section
        List<Color> students = new ArrayList<>(supplySize);
        studentSupply.keySet().forEach(color -> {
            for(int i = 0; i < studentSupply.get(color); i++) students.add(color);
        });

        Color chosen = students.get(Randomizer.nextInt(students.size()));
        studentSupply.put(chosen, studentSupply.get(chosen) - 1);
        return chosen;
    }

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
     * @throws EmptyStudentSupplyException if there aren't 2 students for each color available in the supply
     * @return a list containing the students extracted for the initialization of the archipelagos according to the rulebook
     */
    public Queue<Color> getStudentsForArchipelagosInitialization() throws EmptyStudentSupplyException {
        final int studentsToExtract = 10;
        final int studentsPerColor = 2;
        LinkedList<Color> colors = new LinkedList<>();
        for(Color color : Color.values()) {
            if(this.studentSupply.get(color) < studentsPerColor) throw new EmptyStudentSupplyException();

            // Remove the students from the supply
            this.studentSupply.put(color, this.studentSupply.get(color) - studentsPerColor);

            // Add two students of the same color to the list
            colors.add(color);
            colors.add(color);
        }

        // Randomly sort the queue
        Collections.shuffle(colors);

        return colors;
    }

    public boolean isEmpty() {

        return this.numberOfStudentsLeft() == 0;
    }

    public int numberOfStudentsLeft(){
        return this.studentSupply.values().stream().mapToInt(i -> i).sum();
    }
}
