package code.practice.tasks;

import code.practice.model.Transaction;

import java.time.LocalDateTime;
import java.util.*;

public class TransactionProcessing {
    private List<Transaction> transactionsList = new ArrayList<>();

    /**
     * Given list of integers, need to return list which contains all duplicates from original list.
     * Example:
     * Given: [3, 5, 1 ,5 ,6 ,5 ,4, 4]
     * Return: [5, 5, 4]
     */
    public List<Integer> getDuplicates(List<Integer> numbers) {
        List<Integer> listOfDuplicateElements = new ArrayList<>();
        Set<Integer> uniqueElementsFromNumbersList = new HashSet<>();
        boolean additionToSetResult;
        for (int i = 0; i < numbers.size(); i++) {
            additionToSetResult = uniqueElementsFromNumbersList.add(numbers.get(i));
            if (!additionToSetResult) {
                listOfDuplicateElements.add(numbers.get(i));
            }
        }
        return listOfDuplicateElements;
    }

    //*********************************************************
    //The next task contains two methods
    /**
     * Need to save transaction object in some place. Please check getNewestTransactions method
     * to find the right data structure to save.
     */
    public void addTransaction(Transaction transaction) {
        transactionsList.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactionsList;
    }

    /**
     * Need to return transactions which were saved in the last n seconds.
     */
    public List<Transaction> getNewestTransactions(int seconds) {
        List<Transaction> newestTransactionsList = new ArrayList<>();
        transactionsList.forEach(t -> {
            if (t.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(seconds))) {
                newestTransactionsList.add(t);
            }
        });
        return newestTransactionsList;
    }
}
