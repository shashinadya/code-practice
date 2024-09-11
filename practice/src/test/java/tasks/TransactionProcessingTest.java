package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

public class TransactionProcessingTest {
    private TransactionProcessing transactionProcessing;
    private Transaction transaction1 = new Transaction("Transaction 1", LocalDateTime.now());
    private Transaction transaction2 = new Transaction("Transaction 2", LocalDateTime.now().minusSeconds(5));
    private Transaction transaction3 = new Transaction("Transaction 3", LocalDateTime.now().minusSeconds(15));

    @BeforeEach
    void setUp() {
        transactionProcessing = new TransactionProcessing();
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
        Set<Transaction> result = transactionProcessing.getNewestTransactions(10);
        assertEquals(2, result.size());
        assertTrue(result.contains(transaction1));
        assertTrue(result.contains(transaction2));
    }
}
