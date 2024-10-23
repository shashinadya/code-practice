package database.helper;

import database.exception.CreationDatabaseException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code Settings} class manages application configuration properties, including database
 * connection settings, resource paths, and other system parameters. It loads properties from a
 * specified properties file and provides default values for settings if the file is not found or
 * values are missing.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 */
public class Settings {
    private final Path defaultJsonDatabasePath;
    private Properties properties;
    private String propertyFileName;
    private static final String LIMIT_PROPERTY_NAME = "limit";
    private static final String PORT_PROPERTY_NAME = "port";
    private static final String DB_USERNAME_PROPERTY_NAME = "database.username";
    private static final String DB_PASSWORD_PROPERTY_NAME = "database.password";
    private static final String DB_BASE_URL_PROPERTY_NAME = "database.base.url";
    private static final String DB_NAME_PROPERTY_NAME = "database.name";
    private static final String DATABASE_STORAGE_PATH_PROPERTY_NAME = "database.storage.path";
    private static final String INITIAL_POOL_SIZE_PROPERTY_NAME = "initial.pool.size";
    private static final String MAX_POOL_SIZE_PROPERTY_NAME = "max.pool.size";
    private static final String BATCH_SIZE_PROPERTY_NAME = "batch.size";
    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    static final String DEFAULT_DATABASE_STORAGE_PATH = "database";
    static final int DEFAULT_LIMIT_VALUE = 100;
    static final int DEFAULT_PORT_VALUE = 8080;
    static final String DEFAULT_DB_USERNAME_VALUE = "db_user";
    static final String DEFAULT_DB_PASSWORD_VALUE = "Qwerty!1";
    static final String DEFAULT_DB_BASE_URL_VALUE = "jdbc:mysql://localhost:3306/";
    static final String DEFAULT_DB_NAME_VALUE = "entities";
    static final int DEFAULT_INITIAL_POOL_SIZE_VALUE = 5;
    static final int DEFAULT_MAX_POOL_SIZE_VALUE = 10;
    static final int DEFAULT_BATCH_SIZE_VALUE = 1000;

    /**
     * Creates a {@code Settings} object and loads properties from the specified file.
     * If the file is not found, default values are set for all properties.
     *
     * @param propertyFileName the name of the properties file to load
     * @throws CreationDatabaseException if an error occurs while creating required directories
     */
    public Settings(String propertyFileName) throws CreationDatabaseException {
        defaultJsonDatabasePath = getFilePath(DEFAULT_DATABASE_STORAGE_PATH);
        this.propertyFileName = propertyFileName;
        loadProperties();
    }

    /**
     * Returns the name of the current properties file.
     *
     * @return the properties file name
     */
    public String getPropertyFileName() {
        return propertyFileName;
    }

    /**
     * Sets a new properties file name and reloads properties from the specified file.
     *
     * @param propertyFileName the new properties file name to set
     */
    public void setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        loadProperties();
    }

    /**
     * Retrieves the value of the {@code limit} property, which defines the maximum number
     * of records to fetch. If the property is not set, the default value is returned.
     *
     * @return the limit value
     */
    public int getLimit() {
        return Integer.parseInt(properties.getProperty(LIMIT_PROPERTY_NAME, String.valueOf(DEFAULT_LIMIT_VALUE)));
    }

    /**
     * Returns the path where the JSON-based database should be stored. If the property is not found,
     * the default storage path is used.
     *
     * @return the database storage path as a {@link Path} object
     */
    public Path getDatabasePath() {
        return Path.of(properties.getProperty(DATABASE_STORAGE_PATH_PROPERTY_NAME, defaultJsonDatabasePath.toString()));
    }

    /**
     * Retrieves the port number for the application. If the property is not set, the default port
     * is returned.
     *
     * @return the port number
     */
    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT_PROPERTY_NAME, String.valueOf(DEFAULT_PORT_VALUE)));
    }

    /**
     * Retrieves the database username used for authentication. If not set, the default username is returned.
     *
     * @return the database username
     */
    public String getDatabaseUsername() {
        return properties.getProperty(DB_USERNAME_PROPERTY_NAME, DEFAULT_DB_USERNAME_VALUE);
    }

    /**
     * Retrieves the database password used for authentication. If not set, the default password is returned.
     *
     * @return the database password
     */
    public String getDatabasePassword() {
        return properties.getProperty(DB_PASSWORD_PROPERTY_NAME, DEFAULT_DB_PASSWORD_VALUE);
    }

    /**
     * Retrieves the base URL of the database, which includes the protocol and server address.
     * If the property is not set, the default URL is returned.
     *
     * @return the database base URL
     */
    public String getDatabaseBaseUrl() {
        return properties.getProperty(DB_BASE_URL_PROPERTY_NAME, DEFAULT_DB_BASE_URL_VALUE);
    }

    /**
     * Retrieves the name of the database. If not set, the default name is returned.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return properties.getProperty(DB_NAME_PROPERTY_NAME, DEFAULT_DB_NAME_VALUE);
    }

    /**
     * Retrieves the initial size of the database connection pool. If the property is not set, the default size is returned.
     *
     * @return the initial connection pool size
     */
    public int getInitialPoolSize() {
        return Integer.parseInt(properties.getProperty(INITIAL_POOL_SIZE_PROPERTY_NAME,
                String.valueOf(DEFAULT_INITIAL_POOL_SIZE_VALUE)));
    }

    /**
     * Retrieves the maximum size of the database connection pool. If the property is not set, the default size is returned.
     *
     * @return the maximum connection pool size
     */
    public int getMaxPoolSize() {
        return Integer.parseInt(properties.getProperty(MAX_POOL_SIZE_PROPERTY_NAME,
                String.valueOf(DEFAULT_MAX_POOL_SIZE_VALUE)));
    }

    /**
     * Retrieves the batch size for database operations, such as inserts or updates.
     * If the property is not set, the default batch size is returned.
     *
     * @return the batch size for database operations
     */
    public int getBatchSize() {
        return Integer.parseInt(properties.getProperty(BATCH_SIZE_PROPERTY_NAME,
                String.valueOf(DEFAULT_BATCH_SIZE_VALUE)));
    }

    Path getFilePath(String directoryName) throws CreationDatabaseException {
        URI resourceFolder;
        String pathToResourceFolder;

        try {
            resourceFolder = getClass().getResource(File.separator).toURI();
        } catch (Exception e) {
            LOG.error("JSON file path cannot be retrieved: {}", e.getMessage());
            throw new CreationDatabaseException("URL cannot be converted to URI: " + e.getMessage());
        }

        pathToResourceFolder = Paths.get(resourceFolder).toString();
        Path path = Paths.get(pathToResourceFolder + File.separator + directoryName);

        if (Files.exists(path)) {
            return path;
        }

        try {
            return Files.createDirectory(path);
        } catch (IOException e) {
            LOG.error("Directory {} cannot be created: {}", path, e.getMessage());
            throw new CreationDatabaseException("Directory cannot be created: " + e.getMessage());
        }
    }

    private void loadProperties() {
        properties = new Properties();
        if (propertyFileName == null) {
            return;
        }

        try (var input = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertyFileName)) {
            if (input == null) {
                setDefaultProperties();
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            LOG.error("Properties file cannot be loaded: {}", e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty(DATABASE_STORAGE_PATH_PROPERTY_NAME, defaultJsonDatabasePath.toString());
        properties.setProperty(LIMIT_PROPERTY_NAME, String.valueOf(DEFAULT_LIMIT_VALUE));
    }
}
