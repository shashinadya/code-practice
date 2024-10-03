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
    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    static final String DEFAULT_DATABASE_STORAGE_PATH = "database";
    static final int DEFAULT_LIMIT_VALUE = 100;
    static final int DEFAULT_PORT_VALUE = 8080;
    static final String DEFAULT_DB_USERNAME_VALUE = "db_user";
    static final String DEFAULT_DB_PASSWORD_VALUE = "Qwerty!1";
    static final String DEFAULT_DB_BASE_URL_VALUE = "jdbc:mysql://localhost:3306/";
    static final String DEFAULT_DB_NAME_VALUE = "entities";

    public Settings(String propertyFileName) throws CreationDatabaseException {
        defaultJsonDatabasePath = getFilePath(DEFAULT_DATABASE_STORAGE_PATH);
        this.propertyFileName = propertyFileName;
        loadProperties();
    }

    public String getPropertyFileName() {
        return propertyFileName;
    }

    public void setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
        loadProperties();
    }

    public int getLimit() {
        return Integer.parseInt(properties.getProperty(LIMIT_PROPERTY_NAME, String.valueOf(DEFAULT_LIMIT_VALUE)));
    }

    public Path getDatabasePath() {
        return Path.of(properties.getProperty(DATABASE_STORAGE_PATH_PROPERTY_NAME, defaultJsonDatabasePath.toString()));
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty(PORT_PROPERTY_NAME, String.valueOf(DEFAULT_PORT_VALUE)));
    }

    public String getDatabaseUsername() {
        return properties.getProperty(DB_USERNAME_PROPERTY_NAME, DEFAULT_DB_USERNAME_VALUE);
    }

    public String getDatabasePassword() {
        return properties.getProperty(DB_PASSWORD_PROPERTY_NAME, DEFAULT_DB_PASSWORD_VALUE);
    }

    public String getDatabaseBaseUrl() {
        return properties.getProperty(DB_BASE_URL_PROPERTY_NAME, DEFAULT_DB_BASE_URL_VALUE);
    }

    public String getDatabaseName() {
        return properties.getProperty(DB_NAME_PROPERTY_NAME, DEFAULT_DB_NAME_VALUE);
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
