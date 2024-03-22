package oop.animals.mammals.predators;

import oop.animals.mammals.Mammal;

public abstract class Predator extends Mammal {
    private WeaponType weaponType;

    public WeaponType getWeaponType() {
        return weaponType;
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
}
