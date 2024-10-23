package database.helper;

import org.junit.jupiter.api.Test;

import static database.helper.Settings.DEFAULT_BATCH_SIZE_VALUE;
import static database.helper.Settings.DEFAULT_DATABASE_STORAGE_PATH;
import static database.helper.Settings.DEFAULT_DB_BASE_URL_VALUE;
import static database.helper.Settings.DEFAULT_DB_NAME_VALUE;
import static database.helper.Settings.DEFAULT_DB_PASSWORD_VALUE;
import static database.helper.Settings.DEFAULT_DB_USERNAME_VALUE;
import static database.helper.Settings.DEFAULT_INITIAL_POOL_SIZE_VALUE;
import static database.helper.Settings.DEFAULT_LIMIT_VALUE;
import static database.helper.Settings.DEFAULT_MAX_POOL_SIZE_VALUE;
import static database.helper.Settings.DEFAULT_PORT_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@code Settings} class.
 * <p>
 * This test class verifies the behavior of the {@code Settings} class methods responsible
 * for loading configuration values from a properties file.
 * <p>
 * The tests ensure that if a valid properties file exists, the expected values are retrieved,
 * and when the file or specific properties are missing, default values are returned.
 * <p>
 * Each test initializes the {@code Settings} object with a path to a properties file
 * and asserts that the values returned match the expected values or the default values.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see Settings
 */
class SettingsTest {
    private Settings settings;

    @Test
    void getDatabasePathFromPropertiesTest() {
        settings = new Settings("Db_app_properties_files/applicationFileExist.properties");
        assertEquals("database-test", settings.getDatabasePath().toString());
    }

    @Test
    void getDatabasePathDefaultValueReturnedTest() {
        settings = new Settings("applicationFileNotExist.properties");
        String testDefaultPath = settings.getFilePath(DEFAULT_DATABASE_STORAGE_PATH).toString();

        assertEquals(testDefaultPath,
                settings.getDatabasePath().toString());
    }

    @Test
    void getLimitTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals(100, settings.getLimit());
    }

    @Test
    void getLimitDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_LIMIT_VALUE, settings.getLimit());
    }

    @Test
    void getPortTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals(80, settings.getPort());
    }

    @Test
    void getPortDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_PORT_VALUE, settings.getPort());
    }

    @Test
    void getDatabaseUsernameTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals("test_user", settings.getDatabaseUsername());
    }

    @Test
    void getDatabaseUsernameDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_DB_USERNAME_VALUE, settings.getDatabaseUsername());
    }

    @Test
    void getDatabasePasswordTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals("Qwerty!1", settings.getDatabasePassword());
    }

    @Test
    void getDatabasePasswordDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_DB_PASSWORD_VALUE, settings.getDatabasePassword());
    }

    @Test
    void getDatabaseBaseUrlTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals("jdbc:mysql://localhost:3306/", settings.getDatabaseBaseUrl());
    }

    @Test
    void getDatabaseBaseUrlDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_DB_BASE_URL_VALUE, settings.getDatabaseBaseUrl());
    }

    @Test
    void getDatabaseNameTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals("test_entities", settings.getDatabaseName());
    }

    @Test
    void getDatabaseNameDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_DB_NAME_VALUE, settings.getDatabaseName());
    }

    @Test
    void getInitialPoolSizeTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals(4, settings.getInitialPoolSize());
    }

    @Test
    void getInitialPoolSizeDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_INITIAL_POOL_SIZE_VALUE, settings.getInitialPoolSize());
    }

    @Test
    void getMaxPoolSizeTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals(14, settings.getMaxPoolSize());
    }

    @Test
    void getMaxPoolSizeDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_MAX_POOL_SIZE_VALUE, settings.getMaxPoolSize());
    }

    @Test
    void getBatchSizeTest() {
        settings = new Settings("Db_app_properties_files/application.properties");
        assertEquals(100, settings.getBatchSize());
    }

    @Test
    void getBatchSizeDefaultValueReturnedTest() {
        settings = new Settings("Db_app_properties_files/applicationFileNotExist.properties");
        assertEquals(DEFAULT_BATCH_SIZE_VALUE, settings.getBatchSize());
    }
}
