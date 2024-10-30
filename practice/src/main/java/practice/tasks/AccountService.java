package practice.tasks;

import practice.model.Account;

public class AccountService {

    public synchronized void withdrawMoney(Account acc, int amountToWithdraw) {
        acc.setMoneyAmount(acc.getMoneyAmount() - amountToWithdraw);
    }

    public synchronized void moveMoney(Account accountFrom, Account accountTo, int amount) {
        accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - amount);
        accountTo.setMoneyAmount(accountTo.getMoneyAmount() + amount);
    }
}
