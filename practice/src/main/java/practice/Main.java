package practice;

import practice.model.Account;
import practice.tasks.AccountService;
import practice.tasks.MultithreadingTask;

public class Main {

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
    }
}
