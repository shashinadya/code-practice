package oop.animals.model.mammals.predators.aquatic;

public class KillerWhale extends AquaticPredator {

    public KillerWhale(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
    }

    public KillerWhale() {
        super();
    }
}
