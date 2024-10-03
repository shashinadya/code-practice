package database.entity;

public abstract class BaseEntity {
    private Integer id;

    public BaseEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private Integer id;

        public T withId(Integer id) {
            this.id = id;
            return self();
        }

        protected abstract T self();

        public abstract BaseEntity build();

        protected void setBaseFields(BaseEntity entity) {
            entity.setId(this.id);
        }
    }
}
