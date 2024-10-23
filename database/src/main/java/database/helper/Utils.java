package database.helper;

import database.entity.BaseEntity;
import io.javalin.http.BadRequestResponse;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code Utils} class provides general-purpose utility methods for use throughout the application.
 * <p>
 * This class contains a method for dynamically retrieving all subclasses of {@code BaseEntity}
 * within a specified package.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Utils {

    /**
     * Retrieves all subclasses of {@code BaseEntity} from the {@code database.entity} package.
     *
     * <p>This method scans the {@code database.entity} package for all classes that extend
     * {@code BaseEntity}. It uses the current thread's class loader to locate and load these
     * classes at runtime, and returns them as a set.
     *
     * <p>If the specified package cannot be found, or if any class cannot be loaded, an
     * {@code IllegalArgumentException} or {@code BadRequestResponse} is thrown, respectively.
     *
     * @return a set of classes that extend {@code BaseEntity}, excluding the {@code BaseEntity} class itself
     * @throws IllegalArgumentException if the package cannot be found
     * @throws BadRequestResponse       if any of the classes cannot be loaded
     */
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
