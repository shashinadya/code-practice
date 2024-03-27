package oop.animals.model.reptiles;

import oop.animals.model.Animal;

public abstract class Reptile extends Animal {

    public Reptile(int age, int weight) {
        super(age, weight);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Reptile() {
    }
}
