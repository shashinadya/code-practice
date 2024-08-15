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
    private String propertyFileName;
    private final Path defaultJsonDatabasePath;
    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    static final String DATABASE_STORAGE_PATH = "database.storage.path";
    static final String DEFAULT_DATABASE_STORAGE_PATH = "database";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final int DEFAULT_LIMIT_VALUE = 100;
    public static final int DEFAULT_OFFSET_VALUE = 0;
    Properties properties = new Properties();

    public Settings() throws CreationDatabaseException {
        defaultJsonDatabasePath = getFilePath(DEFAULT_DATABASE_STORAGE_PATH);
    }

    public Settings(String propertyFileName) throws CreationDatabaseException {
        this();
        this.propertyFileName = propertyFileName;
    }

    public String getPropertyFileName() {
        return propertyFileName;
    }

    public void setPropertyFileName(String propertyFileName) {
        this.propertyFileName = propertyFileName;
    }

    public int getLimit() {
        String limitStr = getProperty(LIMIT, String.valueOf(DEFAULT_LIMIT_VALUE));
        return Integer.parseInt(limitStr);
    }

    public int getOffset() {
        String offsetStr = getProperty(OFFSET, String.valueOf(DEFAULT_OFFSET_VALUE));
        return Integer.parseInt(offsetStr);
    }

    public Path getDatabasePath() {
        String databaseStoragePath = getProperty(DATABASE_STORAGE_PATH, defaultJsonDatabasePath.toString());
        return Path.of(databaseStoragePath);
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

    private Properties loadProperties() {
        if (propertyFileName == null) {
            return null;
        }

        try (var input = Settings.class.getClassLoader().getResourceAsStream(propertyFileName)) {
            if (input == null) {
                return null;
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            LOG.error("Properties file cannot be loaded: {}", e.getMessage());
            return null;
        }
    }

    private String getProperty(String propertyName, String defaultValue) {
        Properties properties = loadProperties();
        if (properties == null) {
            return defaultValue;
        }
        return properties.getProperty(propertyName, defaultValue);
    }
}
