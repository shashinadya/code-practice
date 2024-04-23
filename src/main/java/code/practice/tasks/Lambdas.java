package code.practice.tasks;

import oop.animals.model.mammals.predators.aquatic.Cachalot;

import java.util.function.Function;
import java.util.function.Predicate;

import static oop.animals.model.mammals.predators.Predator.WeaponType.TEETH;

public class Lambdas {

    private Predicate<String> predicate = (x) -> x.length() < 5;

    //Default values used for other constructor parameters
    private Function<Integer, Cachalot> mapIntegerToCachalotWeight = (weight) -> new Cachalot(1, weight, TEETH, 0);

    public Function<Integer, Cachalot> getMapIntegerToCachalotWeight() {
        return mapIntegerToCachalotWeight;
    }

    public void setMapIntegerToCachalotWeight(Function<Integer, Cachalot> mapIntegerToCachalotWeight) {
        this.mapIntegerToCachalotWeight = mapIntegerToCachalotWeight;
    }

    public Predicate<String> getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate<String> predicate) {
        this.predicate = predicate;
    }
}
