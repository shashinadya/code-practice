package code.practice;

import code.practice.model.Transaction;
import code.practice.tasks.StringArchiver;
import code.practice.tasks.TransactionProcessing;
import code.practice.tasks.WordCounter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        StringArchiver stringArchiver = new StringArchiver();
        WordCounter wordCounter = new WordCounter();
        System.out.println(stringArchiver.archiveString("aabbb"));
        wordCounter.countWords("Mom soap frame");
    }
}
