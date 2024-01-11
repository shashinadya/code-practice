package code.practice;

import code.practice.tasks.StringArchiver;

public class Main {
    public static void main(String[] args) {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder originalString = new StringBuilder("aabbb");
        System.out.println(stringArchiver.archiveString(originalString));
    }
}
