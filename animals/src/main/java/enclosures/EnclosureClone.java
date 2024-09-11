package enclosures;

import practice.model.mammals.artiodactyls.Cow;

public class EnclosureClone {

    public Enclosure<Cow> cloneEnclosureOfCows(Enclosure<Cow> enclosureToClone) throws CloneNotSupportedException {
        Enclosure<Cow> clonedEnclosure = new Enclosure<>();
        for (Cow cow : enclosureToClone.getAnimals()) {
            clonedEnclosure.addAnimal(cow.clone());
        }
        return clonedEnclosure;
    }
}
