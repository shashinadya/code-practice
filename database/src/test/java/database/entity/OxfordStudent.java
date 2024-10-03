package database.entity;

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

    public static class Builder extends BaseEntity.Builder<OxfordStudent.Builder> {
        private String fullName;
        private Double averageScore;
        private Integer age;

        public OxfordStudent.Builder withFullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public OxfordStudent.Builder withAverageScore(Double averageScore) {
            this.averageScore = averageScore;
            return this;
        }

        public OxfordStudent.Builder withAge(Integer age) {
            this.age = age;
            return this;
        }

        @Override
        protected OxfordStudent.Builder self() {
            return this;
        }

        @Override
        public OxfordStudent build() {
            OxfordStudent student = new OxfordStudent();
            setBaseFields(student);
            student.setFullName(this.fullName);
            student.setAverageScore(this.averageScore);
            student.setAge(this.age);
            return student;
        }
    }
}
