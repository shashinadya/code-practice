package code.practice.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonTasks {
    /**
     * Given map with unique students nicknames and their age, need to return map which contains
     * reverted map where key is an age and value is a nickname(s).
     */
    public Map<Integer, List<String>> reverseMap(Map<String, Integer> studentsAgeByNickname) {
        Map<Integer, List<String>> reversedMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : studentsAgeByNickname.entrySet()) {
            reversedMap.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }
        return reversedMap;
    }

    /**
     * Given list of integers, need to return list which contains all duplicates from original list.
     * Example:
     * Given: [3, 5, 1 ,5 ,6 ,5 ,4, 4]
     * Return: [5, 5, 4]
     */
    public List<Integer> getDuplicates(List<Integer> numbers) {
        List<Integer> listOfDuplicateElements = new ArrayList<>();
        Set<Integer> uniqueElementsFromNumbersList = new HashSet<>();
        for (int number : numbers) {
            if (!uniqueElementsFromNumbersList.add(number)) {
                listOfDuplicateElements.add(number);
            }
        }
        return listOfDuplicateElements;
    }
}
