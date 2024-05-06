package oop.animals.enclosures;

import oop.animals.model.mammals.artiodactyls.Cow;

public class EnclosureClone {

    public Enclosure<Cow> cloneEnclosureOfCows(Enclosure<Cow> enclosureToClone) throws CloneNotSupportedException {
        Enclosure<Cow> clonedEnclosure = new Enclosure<>();
        Cow clonedCow;
        for (Cow cow : enclosureToClone.getAnimals()) {
            clonedCow = cow.clone();
            clonedEnclosure.addAnimal(clonedCow);
        }
        return clonedEnclosure;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
