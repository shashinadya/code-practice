package database.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Course extends BaseEntity {
    private String name;
    private Set<Student> students = new HashSet<>();

    public Course() {
    }

    public Course(String name, List<Student> students) {
        super();
        this.name = name;
        this.students.addAll(students);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name) && Objects.equals(students, course.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, students);
    }
}
