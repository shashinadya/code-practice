package entity;

import java.util.Objects;

public class OxfordStudent extends Student {
    private Integer age;

    public OxfordStudent() {
    }

    public OxfordStudent(String fullName, Double averageScore, Integer age) {
        super(fullName, averageScore);
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OxfordStudent that = (OxfordStudent) o;
        return Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), age);
    }
}
