package code.practice;

import code.practice.tasks.StringArchiver;
import code.practice.tasks.WordCounter;

public class Main {
    public static void main(String[] args) {
        StringArchiver stringArchiver = new StringArchiver();
        WordCounter wordCounter = new WordCounter();
        System.out.println(stringArchiver.archiveString("aabbb"));
        wordCounter.countWords("Mom soap frame");
    }
}
