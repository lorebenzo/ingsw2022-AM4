package it.polimi.ingsw.server.game_logic;

import it.polimi.ingsw.server.game_logic.enums.Color;
import it.polimi.ingsw.server.game_logic.exceptions.EmptyStudentSupplyException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StudentFactoryTest {

    @Test
    public void getStudent() {
        StudentFactory s = new StudentFactory();
        try {
            for(int i = 0; i < 26 * 5; i++) assertTrue(checkColor(s.getStudent()));
        } catch (EmptyStudentSupplyException e) {
            fail();
        }

        try {
            s.getStudent();
            fail();
        } catch (EmptyStudentSupplyException e) {
            assertTrue(true);
        }
    }

    private boolean checkColor(Color c) {
        return Arrays.stream(Color.values()).collect(Collectors.toSet()).contains(c);
    }

    @Test
    public void generateStudent() {
        StudentFactory s = new StudentFactory();
        for(int i = 0; i < 1000; i++) assertTrue(checkColor(s.generateStudent()));
    }

    @Test
    public void getNStudents() {
        StudentFactory s = new StudentFactory();
        List<Color> students;
        try {
            students = s.getNStudents(40);
            assertEquals(40, students.size());
            students.forEach(student -> assertTrue(checkColor(student)));

            students = s.getNStudents(90);
            assertEquals(90, students.size());
            students.forEach(student -> assertTrue(checkColor(student)));
        } catch (EmptyStudentSupplyException e) {
            fail();
        }

        try {
            s.getNStudents(10);
            fail();
        } catch (EmptyStudentSupplyException e) {
            assertTrue(true);
        }
    }
}