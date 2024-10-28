package practice;

import practice.tasks.MultithreadingTask;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        MultithreadingTask task = new MultithreadingTask();
        System.out.println(task.elementsSum(new int[]{1, 2, 3, 4, 5, 6, 7}, 0));
        System.out.println(task.elementsSumParallel(new int[]{1, 2, 3, 4, 5, 6, 7}));
    }
}
