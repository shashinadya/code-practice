package code.practice.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Integer> uniqueElementsFromNumbersList = new HashSet<>();
        return numbers.stream()
                .filter(n -> !uniqueElementsFromNumbersList.add(n))
                .collect(Collectors.toList());
    }
}
