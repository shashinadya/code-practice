package code.practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import code.practice.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionProcessingTest {
    private TransactionProcessing transactionProcessing;
    private Transaction transaction1 = new Transaction("Transaction 1", LocalDateTime.now());
    private Transaction transaction2 = new Transaction("Transaction 2", LocalDateTime.now().minusSeconds(5));
    private Transaction transaction3 = new Transaction("Transaction 3", LocalDateTime.now().minusSeconds(15));

    @BeforeEach
    void setUp() throws Exception {
        transactionProcessing = new TransactionProcessing();
    }

    @Test
    void getDuplicatesTest() {
        List<Integer> numbers = new ArrayList<>(List.of(3, 5, 1, 5, 6, 5, 4, 4, 5));
        assertEquals(transactionProcessing.getDuplicates(numbers), List.of(5, 5, 4, 5));
    }

    @Test
    void addTransactionTest() {
        transactionProcessing.addTransaction(transaction1);
        assertTrue(transactionProcessing.getTransactions().contains(transaction1));
    }

    @Test
    void getNewestTransactionsTest() {
        transactionProcessing.addTransaction(transaction1);
        transactionProcessing.addTransaction(transaction2);
        transactionProcessing.addTransaction(transaction3);
        List<Transaction> result = transactionProcessing.getNewestTransactions(10);
        assertEquals(2, result.size());
    }
}
