package code.practice.tasks;

import code.practice.model.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionProcessing {
    private NavigableSet<Transaction> transactions = new TreeSet<>();

    //*********************************************************
    //The next task contains two methods

    /**
     * Need to save transaction object in some place. Please check getNewestTransactions method
     * to find the right data structure to save.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public NavigableSet<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Need to return transactions which were saved in the last n seconds.
     */
    public Set<Transaction> getNewestTransactions(int seconds) {
        return transactions.tailSet(new Transaction("", LocalDateTime.now().minusSeconds(seconds)));
    }
}
