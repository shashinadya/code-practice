package oop.animals.model.spiders;

import oop.animals.model.Animal;

public abstract class Spider extends Animal {

    public Spider(int age, int weight) {
        super(age, weight);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Spider() {
    }
}
