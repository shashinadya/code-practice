package oop.animals.model.mammals.artiodactyls;

public class Cow extends Artiodactyl implements Cloneable{

    public Cow(int age, int weight, boolean hasHorns) {
        super(age, weight, hasHorns);
    }

    public Cow() {
        super();
    }

    @Override
    public Cow clone() throws CloneNotSupportedException {
        return (Cow) super.clone();
    }
}
