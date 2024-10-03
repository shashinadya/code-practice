package database.helper;

import database.entity.BaseEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class Swagger {
    public static final String DESCRIPTION_HEADER = "The following entities are available:";
    public static final String TITLE = "Database Service OpenAPI";

    public static String buildDescription(Set<Class<? extends BaseEntity>> entities) {
        return "<b>" + DESCRIPTION_HEADER + "</b><br><ul>" + entities.stream()
                .map(Class::getSimpleName)
                .map(entityName -> "<li>" + entityName + "</li>")
                .collect(Collectors.joining()) + "</ul>" +
                "<b>Please note that the real request/response body depends on the entity type</b>";
    }
}
