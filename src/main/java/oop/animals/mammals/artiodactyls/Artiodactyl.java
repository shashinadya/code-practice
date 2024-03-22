package oop.animals.mammals.artiodactyls;

import oop.animals.mammals.Mammal;

public abstract class Artiodactyl extends Mammal {
    private boolean hasHorns;

    public boolean isHasHorns() {
        return hasHorns;
    }

    public void setHasHorns(boolean hasHorns) {
        this.hasHorns = hasHorns;
    }
}
