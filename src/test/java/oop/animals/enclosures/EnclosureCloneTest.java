package oop.animals.enclosures;

import oop.animals.model.mammals.artiodactyls.Cow;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnclosureCloneTest {
    private final Cow cow1 = new Cow(5, 10, true);
    private final Cow cow2 = new Cow(1, 5, false);
    private final Cow cow3 = new Cow(4, 11, true);
    private final Cow cow4 = new Cow(9, 20, true);
    private final Cow clonedCow1 = new Cow(5, 10, true);
    private final Cow clonedCow2 = new Cow(1, 5, false);
    private final Cow clonedCow3 = new Cow(4, 11, true);
    private final Cow clonedCow4 = new Cow(9, 20, true);
    private final Enclosure<Cow> originalEnclosure = new Enclosure<>();
    private final Enclosure<Cow> clonedEnclosure = new Enclosure<>();
    private final EnclosureClone enclosureClone = new EnclosureClone();

    @Test
    public void cloneEnclosureOfCowsTest() throws CloneNotSupportedException {
        originalEnclosure.addAnimals(List.of(cow1, cow2, cow3, cow4));
        clonedEnclosure.addAnimals(List.of(clonedCow1, clonedCow2, clonedCow3, clonedCow4));

        var receivedClonedEnc = enclosureClone.cloneEnclosureOfCows(originalEnclosure);
        assertEquals(clonedEnclosure, receivedClonedEnc);
    }
}
