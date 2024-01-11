package code.practice.tasks;

/**
 * String archiver
 *   * There is a public String archiveString(String s) method, the task of the method is to return a string in the
 *   form that if it has the following consecutively repeated characters, then it is necessary to return a character
 *   and a number, except for those that are not repeated
 *   * examples:
 * abcd -> abcd
 * aabbbccccd -> a2b3c4d
 */
public class StringArchiver {

    public StringBuilder archiveString(StringBuilder str) {
        StringBuilder resultString = new StringBuilder();

        if (str == null) {
            throw new IllegalArgumentException("String is null.");
        }

        if (str.isEmpty()) {
            resultString.append("String is empty.");
            return resultString;
        }

        if (str.length() == 1) {
            return str;
        }

        int word_count = 1;
        char[] charArray = str.toString().toCharArray();
        char lastHandledChar = charArray[0];
        for (int i = 1; i < charArray.length; i++) {
            if (lastHandledChar == charArray[i]) {
                word_count++;
            } else {
                resultString.append(lastHandledChar);
                lastHandledChar = charArray[i];
                if (word_count > 1) {
                    resultString.append(word_count);

                }
                word_count = 1;
            }
        }
        resultString.append(lastHandledChar);
        if (word_count > 1) {
            resultString.append(word_count);
        }
        return resultString;
    }
}



