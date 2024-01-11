package code.practice.tasks;

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
    }

    @Test
    public void archiveStringWhenStringIsEmpty() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder str = new StringBuilder("");
        assertEquals("String is empty.", stringArchiver.archiveString(str).toString());
    }

    @Test
    public void archiveStringWhenStringContainsOnlyOneSymbol() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder originalString = new StringBuilder("a");
        assertEquals(originalString.toString(), stringArchiver.archiveString(originalString).toString());
    }

    @Test
    public void archiveStringWhenAllStringSymbolsAreMetMoreThanOneTime() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder str = new StringBuilder("aabbbccccddddd");
        assertEquals("a2b3c4d5", stringArchiver.archiveString(str).toString());
    }

    @Test
    public void archiveStringWhenAllStringSymbolsAreMetMoreThanOneTimeExceptOneLastSymbol() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder str = new StringBuilder("aabbbccccd");
        assertEquals("a2b3c4d", stringArchiver.archiveString(str).toString());
    }

    @Test
    public void archiveStringWhenOneDistinctSymbolIsLocatedInTheMiddleOfTheString() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder str = new StringBuilder("aabbbcdddddee");
        assertEquals("a2b3cd5e2", stringArchiver.archiveString(str).toString());
    }

    @Test
    public void archiveStringWhenFirstSymbolIsMetOneTime() {
        StringArchiver stringArchiver = new StringArchiver();
        StringBuilder str = new StringBuilder("abbcccdddd");
        assertEquals("ab2c3d4", stringArchiver.archiveString(str).toString());
    }
}
