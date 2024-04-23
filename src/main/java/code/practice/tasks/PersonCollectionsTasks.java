package code.practice.tasks;

import code.practice.model.Person;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonCollectionsTasks {

    /**
     * Filtering and sorting the list:
     * Create a list of objects of a specific class.
     * Use the stream to filter objects by a specific criterion (for example, age > 30).
     * Sort filtered objects by a specific parameter (for example, by name).
     */
    public List<Person> filterAndSortPeopleList(List<Person> people) {
        return people.stream()
                .filter(e -> e.getAge() > 30)
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());
    }

    /**
     * Grouping list items:
     * Create a list of objects with several attributes.
     * Use a stream to group objects by a specific attribute (for example, grouping cities by region).
     */
    public Map<String, List<Person>> groupPeopleByCountry(List<Person> people) {
        return people.stream()
                .collect(Collectors.groupingBy(Person::getCountry));
    }

    /**
     * Converting a list to a map:
     * Create a list of objects that have unique identifiers.
     * Use stream to convert a list into a map using unique identifiers as keys.
     */
    public Map<String, Integer> convertPeopleListToMap(List<Person> people) {
        return people.stream()
                .collect(Collectors.toMap(Person::getName, Person::getAge));
    }

    /**
     * Merging multiple lists into one:
     * Create several lists of objects of the same class.
     * Use the stream to combine these lists into one.
     */
    public List<Person> mergePeopleListWithAnotherList(List<Person> people1, List<Person> people2) {
        return Stream.of(people1, people2)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Removing duplicates from the list:
     * Create a list of objects that include duplicates.
     * Use the stream to remove duplicates from the list.
     */
    public List<Person> removeDuplicatesFromPeopleList(List<Person> people) {
        return people.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Calculating the average value of an attribute:
     * Create a list of objects with a numeric attribute.
     * Use stream to calculate the average value of this attribute.
     */
    public Double findAverageAge(List<Person> people) {
        return people.stream()
                .mapToInt(Person::getAge)
                .average().orElse(0.0);
    }

    /**
     * Converting a list of objects to another data type:
     * Create a list of objects of a specific class.
     * Use a stream to convert objects to another data type (such as a string or another object).
     */
    public List<String> convertIntListToStringList(List<Integer> list) {
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
