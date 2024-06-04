package database.service.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course extends BaseEntity {
    private String name;
    private final List<Student> students = new ArrayList<>();

    public Course() {
    }

    public Course(int id, String name, List<Student> students) {
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

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course aCourse = (Course) o;
        return Objects.equals(name, aCourse.name) && Objects.equals(students, aCourse.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, students);
    }
}
