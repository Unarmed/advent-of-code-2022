package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Day2 {
    public static void main(String[] args) throws Exception{
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day2-step1.txt")));
        String read = reader.readLine();
        int sum = 0;
        while (read != null) {
            sum += calculateScoreFirstStrategy(read);
            read = reader.readLine();
        }
        System.out.println("Sum is: " + sum);
    }

    private static int calculateScoreFirstStrategy(final String read) {
        switch (read) {
            case "A X": return 4;
            case "A Y": return 8;
            case "A Z": return 3;
            case "B X": return 1;
            case "B Y": return 5;
            case "B Z": return 9;
            case "C X": return 7;
            case "C Y": return 2;
            case "C Z": return 6;
            default: throw new RuntimeException("Unexpected strategy: " + read);
        }
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day2-step1.txt")));
        String read = reader.readLine();
        int sum = 0;
        while (read != null) {
            sum += calculateScoreSecondStrategy(read);
            read = reader.readLine();
        }
        System.out.println("Sum is: " + sum);
    }

    private static int calculateScoreSecondStrategy(final String read) {
        switch (read) {
            case "A X": return 3;
            case "A Y": return 4;
            case "A Z": return 8;
            case "B X": return 1;
            case "B Y": return 5;
            case "B Z": return 9;
            case "C X": return 2;
            case "C Y": return 6;
            case "C Z": return 7;
            default: throw new RuntimeException("Unexpected strategy: " + read);
        }
    }
}
