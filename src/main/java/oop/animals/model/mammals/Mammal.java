package oop.animals.model.mammals;

import oop.animals.model.Animal;
import oop.animals.model.mammals.predators.Predator;

public abstract class Mammal extends Animal {

    public Mammal(int age, int weight) {
        super(age, weight);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Mammal() {
    }
}
