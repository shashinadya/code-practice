package database.service.entity;

import java.util.Objects;

public class Student extends BaseEntity {
    private String fullName;
    private Integer classId;
    private Double averageScore;

    public Student(String fullName, Integer classId, Double averageScore) {
        super();
        this.fullName = fullName;
        this.classId = classId;
        this.averageScore = averageScore;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
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
        return Objects.equals(fullName, student.fullName) && Objects.equals(classId, student.classId)
                && Objects.equals(averageScore, student.averageScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, classId, averageScore);
    }
}
