package code.practice.tasks;

import code.practice.model.Person;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PersonCollectionsTasksTest {
    private final PersonCollectionsTasks personCollectionsTasks = new PersonCollectionsTasks();

    private final List<Person> people = List.of(
            new Person("Nadya", 33, "Russia"),
            new Person("Mike", 25, "USA"),
            new Person("Ava", 35, "USA"),
            new Person("Sonia", 21, "Russia"),
            new Person("Barbara", 45, "USA")
    );

    @Test
    void filterAndSortListTest() {
        List<Person> resultPeople = List.of(
                new Person("Ava", 35, "USA"),
                new Person("Barbara", 45, "USA"),
                new Person("Nadya", 33, "Russia")
        );

        assertEquals(resultPeople, personCollectionsTasks.filterAndSortPeopleList(people));
    }

    @Test
    void groupPeopleByCountryTest() {
        Map<String, List<Person>> groupedPeopleByCountry = new HashMap<>();
        groupedPeopleByCountry.put("Russia", List.of(
                new Person("Nadya", 33, "Russia"),
                new Person("Sonia", 21, "Russia")
        ));
        groupedPeopleByCountry.put("USA", List.of(
                new Person("Mike", 25, "USA"),
                new Person("Ava", 35, "USA"),
                new Person("Barbara", 45, "USA")
        ));

        assertEquals(groupedPeopleByCountry, personCollectionsTasks.groupPeopleByCountry(people));
    }

    @Test
    void convertPeopleListToMapTest() {
        Map<String, Integer> convertedPeopleList = new HashMap<>();
        convertedPeopleList.put("Nadya", 33);
        convertedPeopleList.put("Mike", 25);
        convertedPeopleList.put("Ava", 35);
        convertedPeopleList.put("Sonia", 21);
        convertedPeopleList.put("Barbara", 45);

        assertEquals(convertedPeopleList, personCollectionsTasks.convertPeopleListToMap(people));
    }

    @Test
    void mergePeopleListWithAnotherListTest() {
        List<Person> people1 = List.of(
                new Person("Nadya", 33, "Russia"),
                new Person("Mike", 25, "USA")
        );
        List<Person> people2 = List.of(
                new Person("Ava", 35, "USA"),
                new Person("Sonia", 21, "Russia"),
                new Person("Barbara", 45, "USA")
        );

        assertEquals(people, personCollectionsTasks.mergePeopleListWithAnotherList(people1, people2));
    }

    @Test
    void removeDuplicatesFromPeopleListTest() {
        List<Person> peopleWithDuplicates = List.of(
                new Person("Nadya", 33, "Russia"),
                new Person("Mike", 25, "USA"),
                new Person("Nadya", 33, "Russia")
        );
        List<Person> peopleWithoutDuplicates = List.of(
                new Person("Nadya", 33, "Russia"),
                new Person("Mike", 25, "USA")
        );

        assertEquals(peopleWithoutDuplicates,
                personCollectionsTasks.removeDuplicatesFromPeopleList(peopleWithDuplicates));
    }

    @Test
    void findAverageAgeTest() {
        assertEquals(31.8, personCollectionsTasks.findAverageAge(people));
    }

    @Test
    void findAverageAgeIfListIsEmptyTest() {
        List<Person> emptyPeopleList = List.of();

        assertEquals(0.0, personCollectionsTasks.findAverageAge(emptyPeopleList));
    }

    @Test
    void convertIntListToStringListTest() {
        List<Integer> listOfInts = List.of(1, 2, 3, 4, 5);
        List<String> listOfStrings = List.of("1", "2", "3", "4", "5");
        assertEquals(listOfStrings, personCollectionsTasks.convertIntListToStringList(listOfInts));
    }
}
