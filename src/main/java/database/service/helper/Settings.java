package database.service.helper;

import code.practice.exceptions.CriticalDatabaseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

//TODO: logger temporary disabled to do not spam to console, and need to add SLF4J
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

public class Settings {
    private String propertyFileName;
    private final Path defaultJsonDatabasePath;
//    private static final Logger logger = LogManager.getLogger(Settings.class);

    public Settings() throws CriticalDatabaseException {
        defaultJsonDatabasePath = getFilePath("database");
    }

    public Settings(String propertyFileName) throws CriticalDatabaseException {
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

        Properties properties = new Properties();
        InputStream input = Settings.class.getClassLoader().getResourceAsStream(propertyFileName);

        if (input == null) {
            return defaultJsonDatabasePath;
        }
        try {
            properties.load(input);
            String databaseStoragePath = properties.getProperty("database.storage.path");
            return Path.of(databaseStoragePath);
        } catch (IOException e) {
//            logger.error(e);
            e.getMessage();
            return defaultJsonDatabasePath;
        }
    }

    public Path getFilePath(String directoryName) throws CriticalDatabaseException {
        URI resourceFolder;
        String pathToResourceFolder;

        try {
            resourceFolder = getClass().getResource(File.separator).toURI();
        } catch (URISyntaxException e) {
//            logger.error("URL cannot be converted to URI.");
            throw new CriticalDatabaseException("URL cannot be converted to URI: " + e.getMessage());
        }

        pathToResourceFolder = Paths.get(resourceFolder).toString();
        Path path = Paths.get(pathToResourceFolder + File.separator + directoryName);

        if (Files.exists(path)) {
            return path;
        }

        try {
            return Files.createDirectory(path);
        } catch (IOException e) {
//            logger.error("Directory cannot be created.");
            throw new CriticalDatabaseException("Directory cannot be created: " + e.getMessage());
        }
    }
}
