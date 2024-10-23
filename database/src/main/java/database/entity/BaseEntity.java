package database.entity;

/**
 * The {@code BaseEntity} class represents a base entity with a unique identifier ({@code id}) that
 * is inherited by other entity classes. This class provides the foundation for all entities managed
 * in the database, with common behavior and properties related to the entity's ID.
 *
 * <p>This class also defines a nested {@code Builder} class that provides a flexible way to construct
 * instances of {@code BaseEntity} or its subclasses. The builder pattern is used to ensure that
 * entity fields are initialized in a controlled and flexible manner, supporting method chaining.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
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
