package oop.animals.service;

import oop.animals.enclosures.Enclosure;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnclosureServiceTest {
    private EnclosureService<Mammal> enclosureService;

    @BeforeEach
    void setUp() throws Exception {
        enclosureService = new EnclosureService<>();
    }

    @Test
    public void createEnclosureTest() {
        enclosureService.createEnclosure();
        assertEquals(1, enclosureService.getEnclosuresSize());
    }

    @Test
    public void createEnclosureAndAddAnimalTest() {
        Lion lion = new Lion();
        lion.setAge(10);
        lion.setWeight(35);
        lion.setWeaponType(Predator.WeaponType.TEETH);

        enclosureService.createEnclosureAndAddAnimal(lion);
        final Enclosure<Mammal> specificEnclosure = enclosureService.getEnclosures().get(0);

        assertTrue(specificEnclosure.containsOf(lion));
        assertEquals(1, enclosureService.getEnclosuresSize());
        assertEquals(1, specificEnclosure.getAnimals().size());
    }

    @Test
    public void addAnimalToAnySuitableEnclosure() {
        Lion lion = new Lion();
        lion.setAge(10);
        lion.setWeight(35);
        lion.setWeaponType(Predator.WeaponType.TEETH);

        Coyote coyote = new Coyote();
        coyote.setAge(3);
        coyote.setWeight(10);
        coyote.setWeaponType(Predator.WeaponType.CLAWS);

        Cachalot cachalot = new Cachalot();
        cachalot.setAge(15);
        cachalot.setWeight(500);
        cachalot.setWeaponType(Predator.WeaponType.TEETH);

        KillerWhale killerWhale = new KillerWhale();
        killerWhale.setAge(9);
        killerWhale.setWeight(90);
        killerWhale.setWeaponType(Predator.WeaponType.TEETH);

        JungleCat jungleCat = new JungleCat();
        jungleCat.setAge(5);
        jungleCat.setWeight(48);
        jungleCat.setWeaponType(Predator.WeaponType.CLAWS);

        WhiteBear whiteBear = new WhiteBear();
        whiteBear.setAge(20);
        whiteBear.setWeight(160);
        whiteBear.setWeaponType(Predator.WeaponType.CLAWS);

        List<Mammal> animalsOne = new ArrayList<>();
        animalsOne.add(cachalot);
        animalsOne.add(killerWhale);
        animalsOne.add(jungleCat);
        animalsOne.add(whiteBear);

        List<Mammal> animalsTwo = new ArrayList<>();
        animalsTwo.add(coyote);
        animalsTwo.add(cachalot);
        animalsTwo.add(killerWhale);
        animalsTwo.add(jungleCat);
        animalsTwo.add(whiteBear);

        enclosureService.createEnclosureAndAddAnimals(animalsTwo);
        enclosureService.createEnclosureAndAddAnimals(animalsOne);

        enclosureService.addAnimalToAnySuitableEnclosure(lion);
        final Enclosure<Mammal> specificEnclosure = enclosureService.getEnclosures().get(1);

        assertTrue(specificEnclosure.containsOf(lion));
        assertEquals(2, enclosureService.getEnclosuresSize());
        assertEquals(5, specificEnclosure.getAnimals().size());
    }

    @Test
    public void addAnimalToSpecificEnclosureTest() {
        Lion lion = new Lion();
        lion.setAge(10);
        lion.setWeight(35);
        lion.setWeaponType(Predator.WeaponType.TEETH);

        enclosureService.createEnclosure();
        final Enclosure<Mammal> specificEnclosure = enclosureService.getEnclosures().get(0);

        enclosureService.addAnimalToSpecificEnclosure(lion, specificEnclosure);

        assertTrue(specificEnclosure.containsOf(lion));
    }

    @Test
    public void removeEmptyEnclosureTest() {
        enclosureService.createEnclosure();
        enclosureService.removeEmptyEnclosure();

        assertEquals(0, enclosureService.getEnclosures().size());
    }

    @Test
    public void createEnclosureAndAddAnimalsTest() {
        Lion lion = new Lion();
        lion.setAge(10);
        lion.setWeight(35);
        lion.setWeaponType(Predator.WeaponType.TEETH);

        Coyote coyote = new Coyote();
        coyote.setAge(3);
        coyote.setWeight(10);
        coyote.setWeaponType(Predator.WeaponType.CLAWS);

        Cachalot cachalot = new Cachalot();
        cachalot.setAge(15);
        cachalot.setWeight(500);
        cachalot.setWeaponType(Predator.WeaponType.TEETH);

        KillerWhale killerWhale = new KillerWhale();
        killerWhale.setAge(9);
        killerWhale.setWeight(90);
        killerWhale.setWeaponType(Predator.WeaponType.TEETH);

        List<Mammal> animals = new ArrayList<>();
        animals.add(lion);
        animals.add(coyote);
        animals.add(cachalot);
        animals.add(killerWhale);

        enclosureService.createEnclosureAndAddAnimals(animals);
        final Enclosure<Mammal> specificEnclosure = enclosureService.getEnclosures().get(0);

        assertTrue(specificEnclosure.containsOf(lion));
        assertTrue(specificEnclosure.containsOf(coyote));
        assertTrue(specificEnclosure.containsOf(cachalot));
        assertTrue(specificEnclosure.containsOf(killerWhale));
        assertEquals(1, enclosureService.getEnclosuresSize());
        assertEquals(4, specificEnclosure.getAnimals().size());
    }
}
