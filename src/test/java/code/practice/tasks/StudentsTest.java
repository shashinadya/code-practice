package code.practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class StudentsTest {
    private Students students = new Students();

    @Test
    void invertKeyValuesTest() {
        Map<String, Integer> studentsAgeByNickname = new HashMap<>();
        studentsAgeByNickname.put("John", 20);
        studentsAgeByNickname.put("Maria", 22);
        studentsAgeByNickname.put("Nadya", 34);

        Map<Integer, String> invertedMap = new HashMap<>();
        invertedMap.put(20, "John");
        invertedMap.put(22, "Maria");
        invertedMap.put(34, "Nadya");

        assertEquals(invertedMap, students.invertKeyValues(studentsAgeByNickname));
    }
}
