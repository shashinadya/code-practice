package tasks;
/*
 * Write program which causes StackOverflowError. Try to catch and handle this error.
 * */

public class StackOverflowTask {

    public void stackOverflowMethod(int i) {
        try {
            stackOverflowMethod(i);
        } catch (StackOverflowError e) {
            System.out.println("Caught StackOverflowError");
        }
    }
}
