package practice.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultithreadingTask {

    public int elementsSum(int[] numbers, int threads) throws InterruptedException {
        int resultElementsSum = 0;
        if (threads == 0 || threads < 0) {
            System.out.println("Threads amount cannot be zero or negative.");
            for (int number : numbers) {
                resultElementsSum += number;
            }
            return resultElementsSum;
        } else if (threads == 1) {
            for (int number : numbers) {
                resultElementsSum += number;
            }
            return resultElementsSum;
        } else {
            List<int[]> subArrays = divideArray(numbers, threads);
            List<Thread> threadsList = new ArrayList<>();
            int[] subArraysSum = new int[subArrays.size()];
            for (int i = 0; i < threads; i++) {
                String threadName = String.valueOf(i);
                Thread t = new Thread(threadName) {
                    public void run() {
                        int eachThreadSum = 0;
                        int subArrayIndex = Integer.parseInt(threadName);
                        int[] subArray = subArrays.get(subArrayIndex);
                        for (int number : subArray) {
                            eachThreadSum += number;
                        }
                        subArraysSum[subArrayIndex] = eachThreadSum;
                    }
                };
                t.start();
                threadsList.add(t);
            }
            for (Thread t : threadsList) {
                t.join();
            }
            for (int sum : subArraysSum) {
                resultElementsSum += sum;
            }
        }
        return resultElementsSum;
    }

    public int elementsSumParallel(int[] numbers) {
        return Arrays.stream(numbers).parallel().sum();
    }

    private List<int[]> divideArray(int[] array, int n) {
        List<int[]> result = new ArrayList<>();
        int length = array.length;
        int chunkSize = (int) Math.ceil((double) length / n);

        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(length, i + chunkSize);
            int[] chunk = new int[end - i];
            System.arraycopy(array, i, chunk, 0, end - i);
            result.add(chunk);
        }
        return result;
    }
}
