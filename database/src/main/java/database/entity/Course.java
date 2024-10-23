package database.entity;

import java.util.Objects;

/**
 * The {@code Course} class represents a course entity that extends {@link BaseEntity} with
 * an additional field {@code name}, which stores the name of the course. This class provides
 * getters and setters for the course name, as well as methods for object comparison and hashing.
 *
 * <p>This class also includes a nested {@code Builder} class that follows the builder pattern,
 * allowing flexible and controlled construction of {@code Course} instances. The builder
 * supports method chaining for setting fields and ensures that the base entity properties
 * (like {@code id}) are properly initialized.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Course extends BaseEntity {
    private String name;

    public Course() {
    }

    public Course(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public static class Builder extends BaseEntity.Builder<Course.Builder> {
        private String name;

        public Course.Builder withName(String name) {
            this.name = name;
            return this;
        }

        @Override
        protected Course.Builder self() {
            return this;
        }

        @Override
        public Course build() {
            Course course = new Course();
            setBaseFields(course);
            course.setName(this.name);
            return course;
        }
    }
}
