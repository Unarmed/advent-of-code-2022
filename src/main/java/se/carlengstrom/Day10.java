package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day10 {
    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day10-step1.txt")));

        int x = 1;
        int cycle = 1;

        int signalStrengthSums = 0;

        String read = reader.readLine();
        while (read != null) {
            if (read.equals("noop")) {
                signalStrengthSums += maybeEvaluateSignalStrength(cycle, x).orElse(0);
                cycle++;
            } else if (read.startsWith("addx")) {
                int val = Integer.parseInt(read.substring("addx ".length()));
                signalStrengthSums += maybeEvaluateSignalStrength(cycle, x).orElse(0);
                cycle++;
                signalStrengthSums += maybeEvaluateSignalStrength(cycle, x).orElse(0);
                cycle++;
                x += val;
            }

            read = reader.readLine();
        }
        System.out.println("Signal strength sums: " + signalStrengthSums);
    }

    private static Optional<Integer> maybeEvaluateSignalStrength(final int cycle, final int x) {
        if (cycle == 20 || ((cycle - 20) % 40 == 0)) {
            int signalStrength = cycle * x;
            System.out.println("Signal strength in cycle: " + cycle + " is " + signalStrength);
            return Optional.of(signalStrength);
        }
        return Optional.empty();
    }

    private static void part2() throws Exception{
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day10-step1.txt")));

        final int ROW_WIDTH = 40;

        int x = 1;
        int crtPos = 0;

        final List<String> rows = new ArrayList<>();

        String read = reader.readLine();
        String workingString = "";
        while (read != null) {
            if (read.equals("noop")) {
                workingString += Math.abs(crtPos - x) < 2 ? '#' : '.';
                crtPos++;
                if (crtPos >= ROW_WIDTH) {
                    rows.add(workingString);
                    workingString = "";
                    crtPos = 0;
                }
            } else if (read.startsWith("addx")) {
                int val = Integer.parseInt(read.substring("addx ".length()));
                workingString += Math.abs(crtPos - x) < 2 ? '#' : '.';
                crtPos++;
                if (crtPos >= ROW_WIDTH) {
                    rows.add(workingString);
                    workingString = "";
                    crtPos = 0;
                }
                workingString += Math.abs(crtPos - x) < 2 ? '#' : '.';
                crtPos++;
                if (crtPos >= ROW_WIDTH) {
                    rows.add(workingString);
                    workingString = "";
                    crtPos = 0;
                }
                x += val;
            }

            read = reader.readLine();
        }

        for(final String s : rows) {
            System.out.println(s);
        }
    }
}
