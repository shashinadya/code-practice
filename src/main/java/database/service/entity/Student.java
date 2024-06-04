package database.service.entity;

import java.util.Objects;

public class Student extends BaseEntity {
    private String fullName;
    private Integer courseId;
    private Double averageScore;

    public Student() {
    }

    public Student(String fullName, Integer courseId, Double averageScore) {
        super();
        this.fullName = fullName;
        this.courseId = courseId;
        this.averageScore = averageScore;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
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
        return Objects.equals(fullName, student.fullName) && Objects.equals(courseId, student.courseId)
                && Objects.equals(averageScore, student.averageScore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, courseId, averageScore);
    }
}
