package database.entity;

import java.util.List;
import java.util.Objects;

public class Student extends BaseEntity {
    private String fullName;
    private Double averageScore;
    private List<Integer> courseIds;

    public Student() {
    }

    public Student(String fullName, Double averageScore, List<Integer> courseIds) {
        super();
        this.fullName = fullName;
        this.averageScore = averageScore;
        this.courseIds = courseIds;
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

    public List<Integer> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(fullName, student.fullName) && Objects.equals(averageScore, student.averageScore) &&
                Objects.equals(courseIds, student.courseIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, averageScore, courseIds);
    }
}
