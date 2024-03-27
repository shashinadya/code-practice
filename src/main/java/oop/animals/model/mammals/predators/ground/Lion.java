package oop.animals.model.mammals.predators.ground;

public class Lion extends GroundPredator {

    public Lion(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
        this.setWeaponType(weaponType);
    }

    public Lion() {
        super();
    }
}
