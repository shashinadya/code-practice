package code.practice.tasks;

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

        int wordCount = 0;
        for (String word : words) {
            String lowerCaseWord = word.toLowerCase();
            if (wordCountMap.containsKey(lowerCaseWord)) {
                wordCount = wordCountMap.get(lowerCaseWord);
                wordCountMap.put(lowerCaseWord, wordCount + 1);
            } else {
                wordCountMap.put(lowerCaseWord, 1);
            }
        }
        return wordCountMap;
    }
}
