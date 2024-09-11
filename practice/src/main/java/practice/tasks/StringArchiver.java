package practice.tasks;

/**
 * String archiver
 * There is a public String archiveString(String s) method, the task of the method is to return a string in the
 * form that if it has the following consecutively repeated characters, then it is necessary to return a character
 * and a number, except for those that are not repeated
 * examples:
 * abcd -> abcd
 * aabbbccccd -> a2b3c4d
 */
public class StringArchiver {

    public String archiveString(String str) {

        if (str == null) {
            throw new IllegalArgumentException("String is null.");
        }

        if ((str.isEmpty()) || (str.length() == 1)){
            return str;
        }

        StringBuilder resultString = new StringBuilder();

        int wordCount = 1;
        char[] charArray = str.toCharArray();
        char lastHandledChar = charArray[0];
        for (int i = 1; i < charArray.length; i++) {
            if (lastHandledChar == charArray[i]) {
                wordCount++;
            } else {
                resultString.append(lastHandledChar);
                lastHandledChar = charArray[i];
                if (wordCount > 1) {
                    resultString.append(wordCount);

                }
                wordCount = 1;
            }
        }
        resultString.append(lastHandledChar);
        if (wordCount > 1) {
            resultString.append(wordCount);
        }
        return resultString.toString();
    }
}



