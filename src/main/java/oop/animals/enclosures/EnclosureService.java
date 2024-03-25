package oop.animals.enclosures;

import oop.animals.Animal;

import java.util.ArrayList;
import java.util.List;

public class EnclosureService<T extends Animal> implements EnclosureServiceInterface<T> {
    private final List<Enclosure<T>> enclosures = new ArrayList<>();

    private void createEnclosure() {
        Enclosure<T> enclosure = new Enclosure<>();
        enclosures.add(enclosure);
    }

    private void createEnclosureAndAddAnimal(T animal) {
        Enclosure<T> enclosure = new Enclosure<>();
        enclosures.add(enclosure);
        enclosure.addAnimal(animal);
    }

    @Override
    public void addAnimalToSpecificEnclosure(T animal, Enclosure<T> enclosure) {
        enclosure.addAnimal(animal);
    }

    @Override
    public void addAnimalToAnySuitableEnclosure(T animal) {
        for (Enclosure<T> enclosure : enclosures) {
            if (enclosure.getAnimals().size() < 5) {
                enclosure.addAnimal(animal);
            } else {
                createEnclosureAndAddAnimal(animal);
            }
        }
    }

    @Override
    public void removeEmptyEnclosure(Enclosure<T> enclosure) {
        enclosures.removeIf(en -> enclosure.getAnimals().isEmpty());
    }
}
