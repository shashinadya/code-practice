package oop.animals.enclosures;

import oop.animals.model.mammals.Mammal;
import oop.animals.model.mammals.predators.Predator;
import oop.animals.model.mammals.predators.aquatic.Cachalot;
import oop.animals.model.mammals.predators.aquatic.KillerWhale;
import oop.animals.model.mammals.predators.ground.Coyote;
import oop.animals.model.mammals.predators.ground.Lion;
import oop.animals.model.mammals.predators.mixed.JungleCat;
import oop.animals.model.mammals.predators.mixed.WhiteBear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnclosureTest {
    private final Lion lion1 = new Lion(10, 35, Predator.WeaponType.TEETH);
    private final Lion lion2 = new Lion(5, 20, Predator.WeaponType.TEETH);
    private final Coyote coyote = new Coyote(3, 10, Predator.WeaponType.CLAWS);
    private final Cachalot cachalot = new Cachalot(15, 500, Predator.WeaponType.TEETH);
    private final KillerWhale killerWhale = new KillerWhale(9, 90, Predator.WeaponType.TEETH);
    private final JungleCat jungleCat = new JungleCat(5, 48, Predator.WeaponType.CLAWS);
    private final WhiteBear whiteBear = new WhiteBear(20, 160, Predator.WeaponType.CLAWS);
    private Enclosure<Mammal> enclosure;

    @BeforeEach
    void setUp() {
        enclosure = new Enclosure<>();
    }

    @Test
    public void addAnimalTest() {
        enclosure.addAnimal(lion1);
        assertTrue(enclosure.containsOf(lion1));
    }

    @Test
    public void addAnimalsTest() {
        List<Mammal> listOfPredators = new ArrayList<>();
        listOfPredators.add(lion2);
        listOfPredators.add(killerWhale);
        listOfPredators.add(coyote);

        enclosure.addAnimals(listOfPredators);

        assertTrue(enclosure.containsOf(lion2));
        assertTrue(enclosure.containsOf(killerWhale));
        assertTrue(enclosure.containsOf(coyote));
    }

    @Test
    public void removeAnimalTest() {
        List<Mammal> animals = new ArrayList<>();
        animals.add(lion2);
        animals.add(killerWhale);
        animals.add(coyote);

        enclosure.addAnimals(animals);
        enclosure.removeAnimal(lion2);

        assertFalse(enclosure.containsOf(lion2));
        assertTrue(enclosure.containsOf(killerWhale));
        assertTrue(enclosure.containsOf(coyote));
    }

    @Test
    public void getAnimalsTest() {
        List<Mammal> animals = new ArrayList<>();
        animals.add(lion2);
        animals.add(killerWhale);
        animals.add(coyote);

        enclosure.addAnimals(animals);

        assertEquals(animals, enclosure.getAnimals());
    }
}
