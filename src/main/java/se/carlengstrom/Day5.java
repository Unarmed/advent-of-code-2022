package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day5 {
    private static final int NUM_STACKS = 9;
    private static final int DATA_ROWS = 8;

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day5-step1.txt")));
        final Ship ship = parseStacks(reader);
        final List<MoveInstruction> moveInstructions = parseMoves(reader);

        ship.move(moveInstructions);

        System.out.println("Final result: ");
        for (final Stack<Character> stack : ship.stacks) {
            System.out.print(stack.peek());
        }
        System.out.println("");
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day5-step1.txt")));
        final Ship ship = parseStacks(reader);
        final List<MoveInstruction> moveInstructions = parseMoves(reader);

        ship.moveWithCrateMover9001(moveInstructions);

        System.out.println("Final result with CrateMover 9001: ");
        for (final Stack<Character> stack : ship.stacks) {
            System.out.print(stack.peek());
        }
        System.out.println("");
    }

    private static Ship parseStacks(final BufferedReader reader) throws IOException {
        // Note(eng): One can probably get clever here. I won't. I know the input size, I'll hard code
        final List<String> rows = IntStream.range(0, DATA_ROWS)
                .mapToObj(i -> {
                    try {
                        return reader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read. Should never happen");
                    }
                }).collect(Collectors.toList());

        final Ship ship = new Ship();
        for (int i = DATA_ROWS-1; i >= 0; i--) {
            final String row = rows.get(i);
            for (int j = 0; j < NUM_STACKS; j++) {
                int indexIntoText = (j * 4) + 1;
                if (row.length() >= indexIntoText) {
                    char c = row.charAt(indexIntoText);
                    if (c != ' ') {
                        ship.addToStack(j, c);
                    }
                }
            }
        }

        // Throw away the last two rows, they are not useful
        reader.readLine();
        reader.readLine();
        return ship;
    }

    private static List<MoveInstruction> parseMoves(final BufferedReader reader) throws IOException {
        final ArrayList<MoveInstruction> moves = new ArrayList<>();
        final String regex = "move (\\d+) from (\\d+) to (\\d+)";
        final Pattern pattern = Pattern.compile(regex);

        String read = reader.readLine();
        while (read != null) {
            final Matcher matcher = pattern.matcher(read);
            if (!matcher.matches()) {
                throw new RuntimeException("Unexpected move " + read);
            }
            moves.add(new MoveInstruction(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))));

            read = reader.readLine();
        }
        return moves;
    }

    private record MoveInstruction(int amount, int from, int to) {}

    private static class Ship {
        final List<Stack<Character>> stacks;

        public Ship() {
            stacks = new ArrayList<>();
            for ( int i = 0; i < NUM_STACKS; i++) {
                stacks.add(new Stack<>());
            }
        }

        public void addToStack(final int i, final char c) {
            stacks.get(i).push(c);
        }

        public void move(final List<MoveInstruction> moveInstructions) {
            for (final MoveInstruction instruction : moveInstructions) {
                final Stack<Character> from = stacks.get(instruction.from - 1);
                final Stack<Character> to = stacks.get(instruction.to - 1);
                for (int i = 0; i < instruction.amount; i++) {
                    to.push(from.pop());
                }
            }
        }
        public void moveWithCrateMover9001(final List<MoveInstruction> moveInstructions) {
            for (final MoveInstruction instruction : moveInstructions) {
                final Stack<Character> from = stacks.get(instruction.from - 1);
                final Stack<Character> to = stacks.get(instruction.to - 1);
                final Stack<Character> temp = new Stack<>();
                for (int i = 0; i < instruction.amount; i++) {
                    temp.push(from.pop());
                }

                while (!temp.isEmpty()) {
                    to.push(temp.pop());
                }
            }
        }

    }
}
