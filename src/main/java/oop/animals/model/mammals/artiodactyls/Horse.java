package oop.animals.model.mammals.artiodactyls;

public class Horse extends Artiodactyl {

    public Horse(int age, int weight) {
        super(age, weight);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Horse() {
        super();
    }
}
