package practice.tasks.multithreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    Lock lock = new ReentrantLock();

    public void deposit(Account account, int amount) {
        try {
            lock.lock();
            account.setMoneyAmount(account.getMoneyAmount() + amount);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(Account account, int amount) {
        try {
            lock.lock();
            if (amount > account.getMoneyAmount()) {
                lock.unlock();
            }
            account.setMoneyAmount(account.getMoneyAmount() - amount);
        } finally {
            lock.unlock();
        }
    }
}
