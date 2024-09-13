package animals.model.mammals.artiodactyls;

import animals.model.mammals.Mammal;

import java.util.Objects;

public abstract class Artiodactyl extends Mammal {
    private boolean hasHorns;

    public Artiodactyl(int age, int weight, boolean hasHorns) {
        super(age, weight);
        this.hasHorns = hasHorns;
    }

    public Artiodactyl() {
    }

    public boolean isHasHorns() {
        return hasHorns;
    }

    public void setHasHorns(boolean hasHorns) {
        this.hasHorns = hasHorns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Artiodactyl that = (Artiodactyl) o;
        return hasHorns == that.hasHorns;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hasHorns);
    }
}
