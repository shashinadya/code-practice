package oop.animals.model.mammals.predators.aquatic;

import oop.animals.model.mammals.predators.Predator;

import java.util.Objects;

public abstract class AquaticPredator extends Predator {
    private int minWaterTemperature;

    public AquaticPredator(int age, int weight, WeaponType weaponType, int minWaterTemperature) {
        super(age, weight, weaponType);
        this.minWaterTemperature = minWaterTemperature;
    }

    public AquaticPredator() {
    }

    public int getMinWaterTemperature() {
        return minWaterTemperature;
    }

    public void setMinWaterTemperature(int minWaterTemperature) {
        this.minWaterTemperature = minWaterTemperature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AquaticPredator that = (AquaticPredator) o;
        return minWaterTemperature == that.minWaterTemperature;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minWaterTemperature);
    }
}
