package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day3 {

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day3-step1.txt")));
        String read = reader.readLine();
        int sum = 0;
        while (read != null) {
            final char c = appearsInBothCompartements(read);
            final int ascii = c;
            // Prio is is oposite of ASCII, so some converstion needed
            final int prio;
            if (ascii <= 90) { // Uppercase Z
                // Uppercase A is 65, should be 27 so subtract by 38
                prio = ascii - 38;
            } else {
                // Lowercase a is 97, should be 1 so subtract by 96
                prio = ascii - 96;
            }
            sum += prio;
            read = reader.readLine();
        }
        System.out.println("Sum is: " + sum);
    }

    private static char appearsInBothCompartements(final String rucksackString) {
        // Note(eng): Rules say both compartments have same number of items => rucksackString.length() is even
        // Also, only one item type appears in both compartements per rucksack, so as soon as it's foudn
        // one can early return.
        final Set<Character> firstCompartement = new HashSet<>();
        for (int i = 0 ; i < rucksackString.length()/2; i++) {
            firstCompartement.add(rucksackString.charAt(i));
        }

        for (int i = rucksackString.length()/2; i < rucksackString.length(); i++) {
            char c = rucksackString.charAt(i);
            if (firstCompartement.contains(c)) {
                return c;
            }
        }
        throw new RuntimeException("Unexpected rucksack: " + rucksackString);
    }

    public static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day3-step1.txt")));
        int sum = 0;
        while (true) {
            final String first = reader.readLine();
            if (first == null) {
                break;
            }
            final String second = reader.readLine();
            final String third = reader.readLine();
            final char c = findCommonChar(first, second, third);

            final int ascii = c;
            // Prio is is oposite of ASCII, so some converstion needed
            final int prio;
            if (ascii <= 90) { // Uppercase Z
                // Uppercase A is 65, should be 27 so subtract by 38
                prio = ascii - 38;
            } else {
                // Lowercase a is 97, should be 1 so subtract by 96
                prio = ascii - 96;
            }
            sum += prio;
        }

        System.out.println("Sum is: " + sum);
    }

    private static char findCommonChar(final String first, final String second, final String third) {
        final Set<Character> firstSet = first.chars().mapToObj(i -> (char) i).collect(Collectors.toSet());
        final Set<Character> secondSet = second.chars().mapToObj(i -> (char) i).collect(Collectors.toSet());
        final Set<Character> thirdSet = third.chars().mapToObj(i -> (char) i).collect(Collectors.toSet());

        firstSet.retainAll(secondSet);
        firstSet.retainAll(thirdSet);
        if (firstSet.size() != 1) {
            throw new RuntimeException("Unexpected common chars: Set size: " + firstSet.size() + " Input: " + String.join(", ", List.of(first, second, third)));
        }
        return firstSet.stream().findFirst().get();
    }
}
