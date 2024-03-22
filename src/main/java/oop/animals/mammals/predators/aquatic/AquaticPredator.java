package oop.animals.mammals.predators.aquatic;

import oop.animals.mammals.predators.Predator;

public abstract class AquaticPredator extends Predator {
    private int minWaterTemperature;

    public int getMinWaterTemperature() {
        return minWaterTemperature;
    }

    public void setMinWaterTemperature(int minWaterTemperature) {
        this.minWaterTemperature = minWaterTemperature;
    }
}
