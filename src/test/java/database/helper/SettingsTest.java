package database.helper;

import org.junit.jupiter.api.Test;

import static database.helper.Settings.DEFAULT_DATABASE_STORAGE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    private Settings settings;

    @Test
    void getDatabasePathFromPropertiesTest() {
        settings = new Settings("Db_app_properties_files/applicationFileExist.properties");
        assertEquals("database-test", settings.getDatabasePath().toString());
    }

    @Test
    void getDatabasePathDefaultPathReturnedTest() {
        settings = new Settings("applicationFileNotExist.properties");
        String testDefaultPath = settings.getFilePath(DEFAULT_DATABASE_STORAGE_PATH).toString();

        assertEquals(testDefaultPath,
                settings.getDatabasePath().toString());
    }
}
