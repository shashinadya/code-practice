package database.helper;

import database.entity.BaseEntity;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code Swagger} class provides utility methods and constants related to
 * generating descriptions for the OpenAPI specification of the Database Service.
 *
 * <p>It includes a method for dynamically building the description of available entities
 * based on the provided set of entity classes. The description is formatted in HTML for display
 * in API documentation.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Swagger {
    public static final String DESCRIPTION_HEADER = "The following entities are available:";
    public static final String TITLE = "Database Service OpenAPI";

    /**
     * Builds an HTML-formatted description listing the available entities in the API.
     *
     * <p>The description contains a header followed by an unordered list of entity names.
     * It also includes a note indicating that the actual request/response body structure
     * depends on the entity type.
     *
     * @param entities a set of classes that represent the available entities in the API,
     *                 where each class extends {@code BaseEntity}
     * @return an HTML string containing the formatted description of the available entities
     */
    public static String buildDescription(Set<Class<? extends BaseEntity>> entities) {
        return "<b>" + DESCRIPTION_HEADER + "</b><br><ul>" + entities.stream()
                .map(Class::getSimpleName)
                .map(entityName -> "<li>" + entityName + "</li>")
                .collect(Collectors.joining()) + "</ul>" +
                "<b>Please note that the real request/response body depends on the entity type</b>";
    }
}
