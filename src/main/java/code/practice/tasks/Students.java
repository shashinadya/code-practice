package code.practice.tasks;

import java.util.HashMap;
import java.util.Map;

/**
 * Given map with unique students nicknames and their age, need to return map which contains
 * reverted map where key is an age and value is a nickname(s).
 */
public class Students {
    public Map<Integer, String> invertKeyValues(Map<String, Integer> studentsAgeByNickname) {
        Map<Integer, String> invertedMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : studentsAgeByNickname.entrySet()) {
            invertedMap.put(entry.getValue(), entry.getKey());
        }
        return invertedMap;
    }
}
