package database.entity;

import java.util.Objects;

public class OxfordStudent extends Student {
    private int age;

    public OxfordStudent() {
    }

    public OxfordStudent(String fullName, Double averageScore, int age) {
        super(fullName, averageScore);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OxfordStudent that = (OxfordStudent) o;
        return age == that.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), age);
    }
}
