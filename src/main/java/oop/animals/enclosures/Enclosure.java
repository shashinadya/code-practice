package oop.animals.enclosures;

import oop.animals.model.Animal;

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

    public void removeAnimal(T animal) {
        animals.remove(animal);
    }

    public boolean containsOf(T animal) {
        if (animals.contains(animal)) {
            return true;
        } else return false;
    }
}
