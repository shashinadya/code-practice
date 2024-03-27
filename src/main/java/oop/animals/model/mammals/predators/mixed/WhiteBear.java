package oop.animals.model.mammals.predators.mixed;

public class WhiteBear extends MixedPredator {

    public WhiteBear(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
    }

    public WhiteBear() {
        super();
    }
}
