package oop.animals.enclosures;

import oop.animals.model.Animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return animals.contains(animal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enclosure<?> enclosure = (Enclosure<?>) o;
        return Objects.equals(animals, enclosure.animals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(animals);
    }
}
