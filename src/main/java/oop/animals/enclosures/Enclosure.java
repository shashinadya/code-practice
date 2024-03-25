package oop.animals.enclosures;

import oop.animals.Animal;

import java.util.ArrayList;
import java.util.List;

public class Enclosure<T extends Animal> {
    private final List<T> animals = new ArrayList<>();

    public void addAnimal(T animal) {
        animals.add(animal);
    }

    public void addAnimals(List<T> animals) {
        this.animals.addAll(animals);
    }

    public List<T> getAnimals() {
        return animals;
    }

    public void deleteAnimal(T animal) {
        animals.remove(animal);
    }
}
