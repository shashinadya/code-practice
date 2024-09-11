package tasks;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WordCounterTests {

    @Test
    public void countWordsWhenStringIsNull() {
        WordCounter wordCounter = new WordCounter();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wordCounter.countWords(null);
        });
        assertEquals("String cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void countWordsWhenStringIsEmpty() {
        WordCounter wordCounter = new WordCounter();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            wordCounter.countWords("");
        });
        assertEquals("String cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void countWordsWhenStringContainsOnlyOneWordInSentence() {
        WordCounter wordCounter = new WordCounter();
        Map<String, Integer> wordCountMap = new HashMap<>();
        wordCountMap.put("mom", 1);
        assertEquals(wordCountMap, wordCounter.countWords("Mom"));
    }

    @Test
    public void countWordsWhenThereIsNoRepeatableWordsInSentence() {
        WordCounter wordCounter = new WordCounter();
        Map<String, Integer> wordCountMap = new HashMap<>();
        wordCountMap.put("mom", 1);
        wordCountMap.put("soap", 1);
        wordCountMap.put("frame", 1);
        assertEquals(wordCountMap, wordCounter.countWords("Mom soap frame"));
    }

    @Test
    public void countWordsWhenThereIsOneRepeatableWordForOneWordInSentence() {
        WordCounter wordCounter = new WordCounter();
        Map<String, Integer> wordCountMap = new HashMap<>();
        wordCountMap.put("mom", 2);
        wordCountMap.put("soap", 1);
        wordCountMap.put("frame", 1);
        assertEquals(wordCountMap, wordCounter.countWords("Mom soap frame mom"));
    }
}
