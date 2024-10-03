package database.helper;

import database.entity.BaseEntity;
import io.javalin.http.BadRequestResponse;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static Set<Class<? extends BaseEntity>> getSubclassesOfBaseEntity() {
        Set<Class<? extends BaseEntity>> subclasses = new HashSet<>();
        String packageName = "database.entity";
        String path = packageName.replace('.', '/');

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL = classLoader.getResource(path);

        if (packageURL == null) {
            throw new IllegalArgumentException("Package not found: " + packageName);
        }

        File directory = new File(packageURL.getFile());
        if (directory.exists()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
            if (files != null) {
                for (File file : files) {
                    String className = file.getName().replace(".class", "");
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(packageName + "." + className);
                    } catch (ClassNotFoundException e) {
                        throw new BadRequestResponse("Invalid database.entity class: " + className);
                    }
                    if (BaseEntity.class.isAssignableFrom(clazz) && !clazz.equals(BaseEntity.class)) {
                        subclasses.add(clazz.asSubclass(BaseEntity.class));
                    }
                }
            }
        }
        return subclasses;
    }
}
