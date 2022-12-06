package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Day6 {
    public static void main(String[] args) throws Exception{
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day6-step1.txt")));
        final String read = reader.readLine();
        for (int i = 3; i < read.length(); i++) {
            final HashSet<Character> characters = new HashSet<>();
            characters.add(read.charAt(i-3));
            characters.add(read.charAt(i-2));
            characters.add(read.charAt(i-1));
            characters.add(read.charAt(i));

            if (characters.size() == 4) {
                System.out.println("Start of message marker found at: " + (i + 1));
                break;
            }
        }
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day6-step1.txt")));
        final String read = reader.readLine();
        for (int i = 13; i < read.length(); i++) {
            final HashSet<Character> characters = new HashSet<>();
            for (int j = 0; j < 14; j++) {
                characters.add(read.charAt(i - j));
            }

            if (characters.size() == 14) {
                System.out.println("Start of message marker found at: " + (i + 1));
                break;
            }
        }
    }
}
