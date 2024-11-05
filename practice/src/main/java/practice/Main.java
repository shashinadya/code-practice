package practice;

import practice.model.Account;
import practice.tasks.AccountService;
import practice.tasks.BankAccount;
import practice.tasks.MultithreadingTask;

public class Main {
    static Account account = new Account(1, 1000);
    static int amountToChange = 200;
    static BankAccount bankAccount = new BankAccount();

    public static void main(String[] args) throws InterruptedException {
        MultithreadingTask task = new MultithreadingTask();
        System.out.println(task.elementsSum(new int[]{1, 2, 3, 4, 5, 6, 7}, 0));
        System.out.println(task.elementsSumParallel(new int[]{1, 2, 3, 4, 5, 6, 7}));

        AccountService accountService = new AccountService();
        Account acc1 = new Account(1, 1500);
        Account acc2 = new Account(2, 100);

        accountService.withdrawMoney(acc1, 500);
        System.out.println(acc1.getMoneyAmount());

        accountService.moveMoney(acc1, acc2, 200);
        System.out.println(acc1.getMoneyAmount());
        System.out.println(acc2.getMoneyAmount());

        MyThreadDeposit myThreadDeposit1 = new MyThreadDeposit();
        MyThreadDeposit myThreadDeposit2 = new MyThreadDeposit();
        MyThreadWithdraw myThreadWithdraw1 = new MyThreadWithdraw();
        MyThreadWithdraw myThreadWithdraw2 = new MyThreadWithdraw();

        myThreadDeposit1.start();
        myThreadWithdraw1.start();
        myThreadDeposit2.start();
        myThreadWithdraw2.start();
    }

    static class MyThreadDeposit extends Thread {
        @Override
        public void run() {
            bankAccount.deposit(account, amountToChange);
        }
    }

    static class MyThreadWithdraw extends Thread {
        @Override
        public void run() {
            bankAccount.withdraw(account, amountToChange);
        }
    }
}
