package oop.animals.model.mammals.predators.ground;

public class Coyote extends GroundPredator {

    public Coyote(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
        this.setWeaponType(weaponType);
    }

    public Coyote() {
        super();
    }
}
