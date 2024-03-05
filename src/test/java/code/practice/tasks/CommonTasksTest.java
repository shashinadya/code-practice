package code.practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonTasksTest {
    private CommonTasks commonTasks = new CommonTasks();

    @Test
    void invertKeyValuesTest() {
        Map<String, Integer> studentsAgeByNickname = new HashMap<>();
        studentsAgeByNickname.put("John", 20);
        studentsAgeByNickname.put("Maria", 22);
        studentsAgeByNickname.put("Nadya", 34);

        Map<Integer, List<String>> reversedMap = new HashMap<>();
        reversedMap.put(20, List.of("John"));
        reversedMap.put(22, List.of("Maria"));
        reversedMap.put(34, List.of("Nadya"));

        assertEquals(reversedMap, commonTasks.reverseMap(studentsAgeByNickname));
    }

    @Test
    void invertKeyValuesForStudentsWithTheSameAge() {
        Map<String, Integer> studentsAgeByNickname = new HashMap<>();
        studentsAgeByNickname.put("John", 20);
        studentsAgeByNickname.put("Maria", 22);
        studentsAgeByNickname.put("Nadya", 22);

        Map<Integer, List<String>> reversedMap = new HashMap<>();
        reversedMap.put(20, List.of("John"));
        reversedMap.put(22, List.of("Nadya", "Maria"));

        assertEquals(reversedMap, commonTasks.reverseMap(studentsAgeByNickname));
    }

    @Test
    void getDuplicatesTest() {
        List<Integer> numbers = new ArrayList<>(List.of(3, 5, 1, 5, 6, 5, 4, 4, 5));
        assertEquals(commonTasks.getDuplicates(numbers), List.of(5, 5, 4, 5));
    }
}
