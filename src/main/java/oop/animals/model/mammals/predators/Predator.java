package oop.animals.model.mammals.predators;

import oop.animals.model.mammals.Mammal;

import java.util.Objects;

public abstract class Predator extends Mammal {
    private WeaponType weaponType;

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public Predator(int age, int weight, WeaponType weaponType) {
        super(age, weight);
        this.weaponType = weaponType;
    }

    public Predator() {
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    public enum WeaponType {
        CLAWS("Claws"),
        TEETH("Teeth");

        private final String value;

        WeaponType(String value) {
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
        Predator predator = (Predator) o;
        return weaponType == predator.weaponType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weaponType);
    }
}
