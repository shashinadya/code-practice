package animals.service;

import animals.enclosures.Enclosure;
import animals.model.mammals.Mammal;
import animals.model.mammals.predators.aquatic.Cachalot;
import animals.model.mammals.predators.aquatic.KillerWhale;
import animals.model.mammals.predators.ground.Coyote;
import animals.model.mammals.predators.ground.Lion;
import animals.model.mammals.predators.mixed.JungleCat;
import animals.model.mammals.predators.mixed.WhiteBear;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static animals.model.mammals.predators.Predator.WeaponType.CLAWS;
import static animals.model.mammals.predators.Predator.WeaponType.TEETH;
import static animals.model.mammals.predators.mixed.MixedPredator.PreferredHuntingEnvironment.LAND;
import static animals.model.mammals.predators.mixed.MixedPredator.PreferredHuntingEnvironment.WATER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnclosureServiceTest {
    private final Lion lion = new Lion(10, 35, TEETH, 10);
    private final Coyote coyote = new Coyote(3, 10, CLAWS, 15);
    private final Cachalot cachalot = new Cachalot(15, 500, TEETH, 1);
    private final KillerWhale killerWhale = new KillerWhale(9, 90, TEETH, 5);
    private final JungleCat jungleCat = new JungleCat(5, 48, CLAWS, LAND);
    private final WhiteBear whiteBear = new WhiteBear(20, 160, CLAWS, WATER);
    private EnclosureService<Mammal> enclosureService;

    @BeforeEach
    void setUp() {
        enclosureService = new EnclosureService<>();
    }

    @Test
    public void createEnclosureTest() {
        enclosureService.createEnclosure();
        assertEquals(1, enclosureService.getEnclosuresSize());
    }

    @Test
    public void createEnclosureAndAddAnimalTest() {
        enclosureService.createEnclosureAndAddAnimal(lion);
        final Enclosure<Mammal> specificEnclosure = enclosureService.getEnclosures().get(0);

        assertTrue(specificEnclosure.containsOf(lion));
        assertEquals(1, enclosureService.getEnclosuresSize());
        assertEquals(1, specificEnclosure.getAnimals().size());
    }

    @Test
    public void addAnimalToAnySuitableEnclosure() {
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
