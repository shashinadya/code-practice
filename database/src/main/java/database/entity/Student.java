package database.entity;

import java.util.Objects;

public class Student extends BaseEntity {
    private String fullName;
    private Double averageScore;

    public Student() {
    }

    public Student(String fullName, Double averageScore) {
        super();
        this.fullName = fullName;
        this.averageScore = averageScore;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(fullName, student.fullName) && Objects.equals(averageScore, student.averageScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, averageScore);
    }

    @Override
    public String toString() {
        return super.toString() + ", Student{" +
                "fullName='" + fullName + '\'' +
                ", averageScore=" + averageScore +
                '}';
    }
}
