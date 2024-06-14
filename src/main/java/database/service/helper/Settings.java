package database.service.helper;

import code.practice.exceptions.database.CreationDatabaseException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Settings {
    private String propertyFileName;
    private final Path defaultJsonDatabasePath;
    static final String DATABASE_STORAGE_PATH = "database.storage.path";
    static final String DEFAULT_DATABASE_STORAGE_PATH = "database";
    private static final Logger logger = LogManager.getLogger(Settings.class);

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
            logger.error(e.getMessage());
            return defaultJsonDatabasePath;
        }
    }

    public Path getFilePath(String directoryName) throws CreationDatabaseException {
        URI resourceFolder;
        String pathToResourceFolder;

        try {
            resourceFolder = getClass().getResource(File.separator).toURI();
        } catch (URISyntaxException e) {
            logger.error("URL cannot be converted to URI.");
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
            logger.error("Directory cannot be created.");
            throw new CreationDatabaseException("Directory cannot be created: " + e.getMessage());
        }
    }
}
