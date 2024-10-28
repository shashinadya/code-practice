package database.helper;

import database.entity.BaseEntity;
import database.entity.OxfordStudent;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The {@code UtilsTest} class contains unit tests for the {@code Utils} class.
 *
 * <p>This class tests method for dynamically retrieving all subclasses of {@code BaseEntity}
 * within a specified package.
 *
 * @author <a href='mailto:shashinadya@gmail.com'>Nadya Shashina</a>
 * @see Utils
 */
public class UtilsTest {

    @Test
    void getSubclassesOfBaseEntityTest() {
        Set<Class<? extends BaseEntity>> baseEntities = Utils.getSubclassesOfBaseEntity();
        assertEquals(1, baseEntities.size());
        assertEquals(OxfordStudent.class, baseEntities.iterator().next());
    }
}
