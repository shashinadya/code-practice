package oop.animals.enclosures;

import oop.animals.mammals.Mammal;
import oop.animals.mammals.predators.Predator;
import oop.animals.mammals.predators.aquatic.Cachalot;
import oop.animals.mammals.predators.aquatic.KillerWhale;
import oop.animals.mammals.predators.ground.Coyote;
import oop.animals.mammals.predators.ground.Lion;
import oop.animals.mammals.predators.mixed.JungleCat;
import oop.animals.mammals.predators.mixed.WhiteBear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnclosureTest {
    private Lion lion1, lion2;
    private Coyote coyote;
    private Cachalot cachalot;
    private KillerWhale killerWhale;
    private JungleCat jungleCat;
    private WhiteBear whiteBear;
    private Enclosure<Mammal> enclosure;

    @BeforeEach
    void setUp() throws Exception {
        enclosure = new Enclosure<>();

        lion1 = new Lion();
        lion1.setAge(10);
        lion1.setWeight(35);
        lion1.setWeaponType(Predator.WeaponType.TEETH);

        lion2 = new Lion();
        lion2.setAge(5);
        lion2.setWeight(20);
        lion2.setWeaponType(Predator.WeaponType.TEETH);

        coyote = new Coyote();
        coyote.setAge(3);
        coyote.setWeight(10);
        coyote.setWeaponType(Predator.WeaponType.CLAWS);

        cachalot = new Cachalot();
        cachalot.setAge(15);
        cachalot.setWeight(500);
        cachalot.setWeaponType(Predator.WeaponType.TEETH);

        killerWhale = new KillerWhale();
        killerWhale.setAge(9);
        killerWhale.setWeight(90);
        killerWhale.setWeaponType(Predator.WeaponType.TEETH);

        jungleCat = new JungleCat();
        jungleCat.setAge(5);
        jungleCat.setWeight(48);
        jungleCat.setWeaponType(Predator.WeaponType.CLAWS);

        whiteBear = new WhiteBear();
        whiteBear.setAge(20);
        whiteBear.setWeight(160);
        whiteBear.setWeaponType(Predator.WeaponType.CLAWS);
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
