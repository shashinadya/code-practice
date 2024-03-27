package oop.animals.model.mammals.artiodactyls;

public class Cow extends Artiodactyl {

    public Cow(int age, int weight) {
        super(age, weight);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Cow() {
        super();
    }
}
