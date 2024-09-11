package practice.tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringArchiverTests {

    @Test
    public void archiveStringWhenStringIsNull() {
        StringArchiver stringArchiver = new StringArchiver();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            stringArchiver.archiveString(null);
        });
        assertEquals("String is null.", exception.getMessage());
    }

    @Test
    public void archiveStringWhenStringIsEmpty() {
        StringArchiver stringArchiver = new StringArchiver();
        String emptyString = "";
        assertEquals(emptyString, stringArchiver.archiveString(emptyString));
    }

    @Test
    public void archiveStringWhenStringContainsOnlyOneSymbol() {
        StringArchiver stringArchiver = new StringArchiver();
        String originalString = "a";
        assertEquals(originalString, stringArchiver.archiveString(originalString));
    }

    @Test
    public void archiveStringWhenAllStringSymbolsAreMetMoreThanOneTime() {
        StringArchiver stringArchiver = new StringArchiver();
        assertEquals("a2b3c4d5", stringArchiver.archiveString("aabbbccccddddd"));
    }

    @Test
    public void archiveStringWhenAllStringSymbolsAreMetMoreThanOneTimeExceptOneLastSymbol() {
        StringArchiver stringArchiver = new StringArchiver();
        assertEquals("a2b3c4d", stringArchiver.archiveString("aabbbccccd"));
    }

    @Test
    public void archiveStringWhenOneDistinctSymbolIsLocatedInTheMiddleOfTheString() {
        StringArchiver stringArchiver = new StringArchiver();
        assertEquals("a2b3cd5e2", stringArchiver.archiveString("aabbbcdddddee"));
    }

    @Test
    public void archiveStringWhenFirstSymbolIsMetOneTime() {
        StringArchiver stringArchiver = new StringArchiver();
        assertEquals("ab2c3d4", stringArchiver.archiveString("abbcccdddd"));
    }
}
