package oop.animals.model.mammals.predators.ground;

import code.practice.exceptions.IncorrectGroundMobilityRangeException;
import oop.animals.model.mammals.predators.Predator;

public abstract class GroundPredator extends Predator {
    private int groundMobility;

    public int getGroundMobility() {
        return groundMobility;
    }

    public void setGroundMobility(int groundMobility) {
        if (groundMobility > 0 && groundMobility <= 10) {
            this.groundMobility = groundMobility;
        } else {
            throw new IncorrectGroundMobilityRangeException("groundMobility should be in range of 1-10");
        }
    }
}
