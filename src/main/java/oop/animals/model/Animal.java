package oop.animals.model;

import java.util.Objects;

public abstract class Animal {
    private int age;
    private int weight;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Animal(int age, int weight) {
        this.age = age;
        this.weight = weight;
    }

    public Animal() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return age == animal.age && weight == animal.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, weight);
    }
}
