package oop.animals.model.mammals.artiodactyls;

import oop.animals.model.mammals.Mammal;

public abstract class Artiodactyl extends Mammal {
    private boolean hasHorns;

    public Artiodactyl(int age, int weight) {
        super();
    }

    public Artiodactyl() {
    }

    public boolean isHasHorns() {
        return hasHorns;
    }

    public void setHasHorns(boolean hasHorns) {
        this.hasHorns = hasHorns;
    }
}
