package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;

import java.util.*;
import java.util.stream.Collectors;

public class StudentFactory {
    private final Map<Color, Integer> studentSupply;
    private final Random randomizer;

    public StudentFactory() {
        this.studentSupply = new HashMap<>();
        int initialStudentsPerColor = 26;
        for(Color color : Color.values())
            studentSupply.put(color, initialStudentsPerColor);
        this.randomizer = new Random();
    }

    /**
     *
     * @return a random student drawn from the supply, the student is finally removed from the supply. If the supply is empty throws an exception.
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

        Color chosen = students.get(this.randomizer.nextInt(students.size()));
        studentSupply.put(chosen, studentSupply.get(chosen) - 1);
        return chosen;
    }

    /**
     *
     * @return a random student, each color is equally likely to spawn, this method DOES NOT update the studentSupply
     */
    public Color generateStudent() {
        return Color.values()[this.randomizer.nextInt(Color.values().length)];
    }

    /**
     * @requires n >= 0
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
}
