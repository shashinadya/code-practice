package animals.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import animals.task.Lambdas;
import org.junit.jupiter.api.Test;

import animals.model.mammals.predators.aquatic.Cachalot;

import static animals.model.mammals.predators.Predator.WeaponType.TEETH;

import java.util.List;
import java.util.stream.Collectors;

class LambdasTest {
    private final Lambdas lambdas = new Lambdas();

    @Test
    void predicateForStringTest() {
        String str1 = "Hello, ";
        String str2 = "World";
        String str3 = "!";
        List<String> list = List.of(str1, str2, str3);

        List<String> listOfResultStrings = list.stream()
                .filter(lambdas.getPredicate())
                .collect(Collectors.toList());

        assertEquals(List.of(str3), listOfResultStrings);
    }

    @Test
    void getMapIntegerToCachalotWeightTest() {
        List<Cachalot> expectedCachalots = List.of(
                new Cachalot(1, 1, TEETH, 0),
                new Cachalot(1, 2, TEETH, 0),
                new Cachalot(1, 3, TEETH, 0)
        );
        List<Integer> list = List.of(1, 2, 3);

        var cachalots = list.stream()
                .map(lambdas.getMapIntegerToCachalotWeight())
                .toList();

        assertEquals(expectedCachalots, cachalots);
    }
}
