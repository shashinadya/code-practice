package oop.animals.enclosures;

import oop.animals.Animal;

import java.util.ArrayList;
import java.util.List;

public class EnclosureService<T extends Animal> implements EnclosureServiceInterface<T> {
    private final List<Enclosure<T>> enclosures = new ArrayList<>();

    @Override
    public void addAnimalToSpecificEnclosure(T animal, Enclosure<T> enclosure) {
        enclosure.addAnimal(animal);
    }

    @Override
    public void addAnimalToAnySuitableEnclosure(T animal) {
        for (Enclosure<T> enclosure : enclosures) {
            if (enclosure.getAnimals().size() < 5) {
                enclosure.addAnimal(animal);
                return;
            }
        }
        createEnclosureAndAddAnimal(animal);
    }

    @Override
    public void removeEmptyEnclosure() {
        enclosures.removeIf(enclosure -> enclosure.getAnimals().isEmpty());
    }

    @Override
    public void createEnclosure() {
        Enclosure<T> enclosure = new Enclosure<>();
        enclosures.add(enclosure);
    }

    @Override
    public void createEnclosureAndAddAnimal(T animal) {
        Enclosure<T> enclosure = new Enclosure<>();
        enclosures.add(enclosure);
        enclosure.addAnimal(animal);
    }

    @Override
    public void createEnclosureAndAddAnimals(List<T> animals) {
        Enclosure<T> enclosure = new Enclosure<>();
        enclosures.add(enclosure);
        enclosure.addAnimals(animals);
    }

    @Override
    public int getEnclosuresSize() {
        return enclosures.size();
    }

    @Override
    public List<Enclosure<T>> getEnclosures() {
        return enclosures;
    }
}
