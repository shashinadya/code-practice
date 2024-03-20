package oop.animals.mammals.predators.ground;

import oop.animals.mammals.predators.Predator;

public abstract class GroundPredator extends Predator {

    private int groundMobility;

    public int getGroundMobility() {
        return groundMobility;
    }

    public void setGroundMobility(int groundMobility) {
        if (groundMobility > 0 && groundMobility <= 10) {
            this.groundMobility = groundMobility;
        } else {
            System.out.println("groundMobility should be in range of 1-10");
        }
    }
}
