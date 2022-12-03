package se.carlengstrom;

import java.io.*;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Day1 {

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }



    private static void part1() throws Exception{
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day1-step1.txt")));
        int currentElf = 1;
        int sum = 0;

        int bestElf = -1;
        int bestSum = 0;
        String read = reader.readLine();
        while (read != null) {
            if (read.trim().isEmpty()) {
                if (sum > bestSum) {
                    bestElf = currentElf;
                    bestSum = sum;
                }

                currentElf++;
                sum = 0;
            } else {
                sum += Integer.parseInt(read);
            }
            read = reader.readLine();
        }

        // EOF reached, check last elf
        if (sum > bestSum) {
            bestElf = currentElf;
            bestSum = sum;
        }

        currentElf++;

        System.out.println("Elf " + bestElf + " has the most calories (" + bestSum + ")");
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day1-step1.txt")));
        int sum = 0;

        final PriorityQueue<Integer> mostCalories = new PriorityQueue<>();

        String read = reader.readLine();
        while (read != null) {
            if (read.trim().isEmpty()) {
                mostCalories.add(sum);

                // Discard lowest
                if (mostCalories.size() > 3) {
                    mostCalories.poll();
                }

                sum = 0;
            } else {
                sum += Integer.parseInt(read);
            }
            read = reader.readLine();
        }

        mostCalories.add(sum);

        // Discard lowest
        if (mostCalories.size() > 3) {
            mostCalories.poll();
        }

        int total = mostCalories.stream().reduce(Integer::sum).get();
        System.out.println("Total calories by top 3 elves: " + total + " (" + String.join(",", mostCalories
                .stream()
                .map(i -> Integer.toString(i))
                .collect(Collectors.toList())) +")");
    }
}
