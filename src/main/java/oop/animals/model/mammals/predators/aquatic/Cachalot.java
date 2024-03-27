package oop.animals.model.mammals.predators.aquatic;

public class Cachalot extends AquaticPredator {

    public Cachalot(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
    }

    public Cachalot() {
        super();
    }
}
