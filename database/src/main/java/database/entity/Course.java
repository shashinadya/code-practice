package database.entity;

import java.util.Objects;

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

    @Override
    public String toString() {
        return super.toString() + ", Course{" +
                "name='" + name + '\'' +
                '}';
    }
}
