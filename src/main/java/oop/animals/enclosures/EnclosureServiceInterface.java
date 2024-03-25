package oop.animals.enclosures;

import oop.animals.Animal;

public interface EnclosureServiceInterface<T extends Animal> {

    void addAnimalToSpecificEnclosure(T animal, Enclosure<T> enclosure);

    void addAnimalToAnySuitableEnclosure(T animal);

    void removeEmptyEnclosure(Enclosure<T> enclosure);
}
