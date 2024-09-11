package animals.model.mammals.predators.ground;

import animals.exception.IncorrectGroundMobilityRangeException;
import animals.model.mammals.predators.Predator;

import java.util.Objects;

public abstract class GroundPredator extends Predator {
    private int groundMobility;

    public GroundPredator(int age, int weight, WeaponType weaponType, int groundMobility) {
        super(age, weight, weaponType);
        this.groundMobility = groundMobility;
    }

    public GroundPredator() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroundPredator that = (GroundPredator) o;
        return groundMobility == that.groundMobility;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groundMobility);
    }
}
