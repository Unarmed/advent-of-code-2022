package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 {
    public static void main(String[] args) throws Exception{
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day4-step1.txt")));
        final String regex = "(\\d+)-(\\d+),(\\d+)-(\\d+)";
        final Pattern pattern = Pattern.compile(regex);

        int overlaps = 0;
        String read = reader.readLine();
        while(read != null) {
            final Matcher matcher = pattern.matcher(read);
            if (!matcher.find()) {
                throw new RuntimeException("Unexpected assignment: " + read);
            }

            int firstElfLower = Integer.parseInt(matcher.group(1));
            int firstElfHigher = Integer.parseInt(matcher.group(2));
            int secondElfLower = Integer.parseInt(matcher.group(3));
            int secondElfHigher = Integer.parseInt(matcher.group(4));

            // First elf contains second, then reverse
            if (firstElfLower <= secondElfLower && firstElfHigher >= secondElfHigher) {
                overlaps++;
            } else if (secondElfLower <= firstElfLower && secondElfHigher >= firstElfHigher) {
                overlaps++;
            }

            read = reader.readLine();
        }

        System.out.println("Total overlaps: " + overlaps);
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day4-step1.txt")));
        final String regex = "(\\d+)-(\\d+),(\\d+)-(\\d+)";
        final Pattern pattern = Pattern.compile(regex);

        int overlaps = 0;
        String read = reader.readLine();
        while(read != null) {
            final Matcher matcher = pattern.matcher(read);
            if (!matcher.find()) {
                throw new RuntimeException("Unexpected assignment: " + read);
            }

            int firstElfLower = Integer.parseInt(matcher.group(1));
            int firstElfHigher = Integer.parseInt(matcher.group(2));
            int secondElfLower = Integer.parseInt(matcher.group(3));
            int secondElfHigher = Integer.parseInt(matcher.group(4));

            if (
                 (secondElfLower >= firstElfLower && secondElfLower <= firstElfHigher) || // elf 2 low point inside elf 1 range
                 (secondElfHigher >= firstElfLower && secondElfHigher <= firstElfHigher) || // elf 2 high point inside elf 1 range
                 (secondElfLower < firstElfLower && secondElfHigher > firstElfHigher) // elf 2 completely covers elf 1 range
            ) {
                overlaps++;
            }

            read = reader.readLine();
        }

        System.out.println("Total overlaps: " + overlaps);
    }
}
