package oop.animals.model.mammals.predators.aquatic;

import oop.animals.model.mammals.predators.Predator;

public abstract class AquaticPredator extends Predator {
    private int minWaterTemperature;

    public AquaticPredator(int age, int weight, WeaponType weaponType) {
        super(age, weight, weaponType);
        this.setAge(age);
        this.setWeight(weight);
        this.setWeaponType(weaponType);
    }

    public AquaticPredator() {
    }

    public int getMinWaterTemperature() {
        return minWaterTemperature;
    }

    public void setMinWaterTemperature(int minWaterTemperature) {
        this.minWaterTemperature = minWaterTemperature;
    }
}
