package database.helper;

import database.exception.exception.CreationDatabaseException;

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

    public Path getDatabasePath() {
        if (propertyFileName == null) {
            return defaultJsonDatabasePath;
        }

        var properties = new Properties();
        var input = Settings.class.getClassLoader().getResourceAsStream(propertyFileName);

        if (input == null) {
            return defaultJsonDatabasePath;
        }
        try {
            properties.load(input);
            String databaseStoragePath = properties.getProperty(DATABASE_STORAGE_PATH);
            return Path.of(databaseStoragePath);
        } catch (IOException e) {
            LOG.error("Database path cannot be retrieved: {}", e.getMessage());
            return defaultJsonDatabasePath;
        }
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
}
