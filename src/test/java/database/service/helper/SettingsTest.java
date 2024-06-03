package database.service.helper;

import code.practice.exceptions.CriticalDatabaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    private Settings settings;

    @Test
    void getDatabasePathFromPropertiesTest() throws CriticalDatabaseException {
        settings = new Settings("Db_app_properties_files/applicationFileExist.properties");
        assertEquals("D:\\Files", settings.getDatabasePath().toString());
    }

    @Test
    void getDatabasePathDefaultPathReturnedTest() throws CriticalDatabaseException {
        settings = new Settings("applicationFileNotExist.properties");
        String testDefaultPath = settings.getFilePath("database").toString();

        assertEquals(testDefaultPath,
                settings.getDatabasePath().toString());
    }
}
