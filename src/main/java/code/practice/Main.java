package code.practice;

import code.practice.tasks.StackOverflowTask;

public class Main {
    public static void main(String[] args) {
        /*StringArchiver stringArchiver = new StringArchiver();
        WordCounter wordCounter = new WordCounter();
        System.out.println(stringArchiver.archiveString("aabbb"));
        wordCounter.countWords("Mom soap frame");*/

        StackOverflowTask stackOverflowTask = new StackOverflowTask();
        stackOverflowTask.stackOverflowMethod(2);
    }
}
