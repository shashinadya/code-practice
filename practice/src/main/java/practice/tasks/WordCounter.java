package practice.tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The method accepts a string that contains a sentence. Get the number of identical words that are in this
 * sentence in the form word+count. A sentence can only have spaces between words
 * Example:
 * Mom washed the frame ->
 * mom: 1
 * soap : 1
 * frame: 1
 */
public class WordCounter {
    public Map<String, Integer> countWords(String str) {
        if ((str == null) || (str.isEmpty())) {
            throw new IllegalArgumentException("String cannot be null or empty.");
        }
        Map<String, Integer> wordCountMap = new HashMap<>();
        String[] words = str.split(" ");
        Arrays.stream(words).map(String::toLowerCase).forEach(w -> wordCountMap.merge(w, 1, Integer::sum));
        return wordCountMap;
    }
}
