package model.mammals.predators.mixed;

import model.mammals.predators.Predator;

import java.util.Objects;

public abstract class MixedPredator extends Predator {
    private PreferredHuntingEnvironment preferredHuntingEnvironment;

    public MixedPredator(int age, int weight, WeaponType weaponType,
                         PreferredHuntingEnvironment preferredHuntingEnvironment) {
        super(age, weight, weaponType);
        this.preferredHuntingEnvironment = preferredHuntingEnvironment;
    }

    public MixedPredator() {
    }

    public PreferredHuntingEnvironment getPreferredHuntingEnvironment() {
        return preferredHuntingEnvironment;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MixedPredator that = (MixedPredator) o;
        return preferredHuntingEnvironment == that.preferredHuntingEnvironment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), preferredHuntingEnvironment);
    }
}
