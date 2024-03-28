package oop.animals.enclosures;

import oop.animals.model.mammals.Mammal;
import oop.animals.model.mammals.predators.Predator;
import oop.animals.model.mammals.predators.aquatic.KillerWhale;
import oop.animals.model.mammals.predators.ground.Coyote;
import oop.animals.model.mammals.predators.ground.Lion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnclosureTest {
    private static final Predator.WeaponType WEAPON_TYPE_TEETH = Predator.WeaponType.TEETH;
    private static final Predator.WeaponType WEAPON_TYPE_CLAWS = Predator.WeaponType.CLAWS;
    private final Lion lion1 = new Lion(10, 35, WEAPON_TYPE_TEETH, 10);
    private final Lion lion2 = new Lion(5, 20, WEAPON_TYPE_TEETH, 5);
    private final Coyote coyote = new Coyote(3, 10, WEAPON_TYPE_CLAWS, 15);
    private final KillerWhale killerWhale = new KillerWhale(9, 90, WEAPON_TYPE_TEETH, 5);
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
