package oop.animals.model.mammals.predators.mixed;

public class JungleCat extends MixedPredator {

    public JungleCat(int age, int weight, WeaponType weaponType,
                     PreferredHuntingEnvironment preferredHuntingEnvironment) {
        super(age, weight, weaponType, preferredHuntingEnvironment);
    }

    public JungleCat() {
        super();
    }
}
