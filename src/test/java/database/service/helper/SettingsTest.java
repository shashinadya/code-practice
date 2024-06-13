package database.service.helper;

import code.practice.exceptions.database.CreationDatabaseException;
import org.junit.jupiter.api.Test;

import static database.service.helper.Settings.DEFAULT_DATABASE_STORAGE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    private Settings settings;

    @Test
    void getDatabasePathFromPropertiesTest() throws CreationDatabaseException {
        settings = new Settings("Db_app_properties_files/applicationFileExist.properties");
        assertEquals("database-test", settings.getDatabasePath().toString());
    }

    @Test
    void getDatabasePathDefaultPathReturnedTest() throws CreationDatabaseException {
        settings = new Settings("applicationFileNotExist.properties");
        String testDefaultPath = settings.getFilePath(DEFAULT_DATABASE_STORAGE_PATH).toString();

        assertEquals(testDefaultPath,
                settings.getDatabasePath().toString());
    }
}
