package oop.animals.model.mammals.predators.mixed;

public class JungleCat extends MixedPredator {

    public JungleCat(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
    }

    public JungleCat() {
        super();
    }
}
