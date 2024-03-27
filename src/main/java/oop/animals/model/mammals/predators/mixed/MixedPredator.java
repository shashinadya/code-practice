package oop.animals.model.mammals.predators.mixed;

import oop.animals.model.mammals.predators.Predator;

public abstract class MixedPredator extends Predator {
    private PreferredHuntingEnvironment preferredHuntingEnvironment;

    public PreferredHuntingEnvironment getPreferredHuntingEnvironment() {
        return preferredHuntingEnvironment;
    }

    public MixedPredator(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
        this.setWeaponType(weaponType);
    }

    public MixedPredator() {
    }

    public void setPreferredHuntingEnvironment(PreferredHuntingEnvironment preferredHuntingEnvironment) {
        this.preferredHuntingEnvironment = preferredHuntingEnvironment;
    }

    public enum PreferredHuntingEnvironment {
        WATER("Water"),
        LAND("Land");

        private final String value;

        PreferredHuntingEnvironment(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
