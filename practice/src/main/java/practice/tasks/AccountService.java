package practice.tasks;

import practice.model.Account;

public class AccountService {

    public void withdrawMoney(Account acc, int amountToWithdraw) {
        synchronized (acc) {
            acc.setMoneyAmount(acc.getMoneyAmount() - amountToWithdraw);
        }
    }

    public void moveMoney(Account accountFrom, Account accountTo, int amount) {
        Account firstLock = accountFrom;
        Account secondLock = accountTo;

        if (accountFrom.hashCode() < accountTo.hashCode()) {
            firstLock = accountTo;
            secondLock = accountFrom;
        }

        synchronized (firstLock) {
            synchronized (secondLock) {
                accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
                accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amount);
            }
        }
    }
}
