package database.service;

import enclosures.Enclosure;
import practice.model.Animal;

import java.util.List;

public interface EnclosureServiceInterface<T extends Animal> {

    void addAnimalToSpecificEnclosure(T animal, Enclosure<T> enclosure);

    void addAnimalToAnySuitableEnclosure(T animal);

    void removeEmptyEnclosure();

    void createEnclosure();

    void createEnclosureAndAddAnimal(T animal);

    int getEnclosuresSize();

    List<Enclosure<T>> getEnclosures();

    void createEnclosureAndAddAnimals(List<T> animals);
}
