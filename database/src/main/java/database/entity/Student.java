package database.entity;

import java.util.Objects;

/**
 * The {@code Student} class represents a student entity that extends {@link BaseEntity} and includes
 * additional fields such as {@code fullName} and {@code averageScore}. This class provides
 * getter and setter methods to access and modify these fields, as well as methods for object comparison
 * and hashing.
 *
 * <p>The {@code Builder} class follows the builder pattern, providing a flexible and controlled way to
 * construct {@code Student} objects with method chaining.
 *
 * <p>Typical usage:
 * <pre>
 * {@code
 * Student student = new Student.Builder()
 *                     .withId(1)
 *                     .withFullName("John Doe")
 *                     .withAverageScore(85.5)
 *                     .build();
 * }
 * </pre>
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
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

    public static class Builder extends BaseEntity.Builder<Builder> {
        private String fullName;
        private Double averageScore;

        public Builder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder withAverageScore(Double averageScore) {
            this.averageScore = averageScore;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Student build() {
            Student student = new Student();
            setBaseFields(student);
            student.setFullName(this.fullName);
            student.setAverageScore(this.averageScore);
            return student;
        }
    }
}
