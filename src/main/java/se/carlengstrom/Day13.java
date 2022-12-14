package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static se.carlengstrom.Day13.TriStateBool.*;

public class Day13 {

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final List<ListOrInteger> parsed = parse();

        int sum = 0;
        for(int i = 0; i < parsed.size(); i+=2) {
            final ListOrInteger left = parsed.get(i);
            final ListOrInteger right = parsed.get(i+1);

            final TriStateBool result = ListOrInteger.compareTwo(left, right);
            if (result == TRUE) {
                final int index = (i / 2) + 1;
                sum += index;
            } else if (result == UNCLEAR) {
                throw new RuntimeException("Index " + (i / 2) + 1 + " produced unclear result!");
            }
        }
        System.out.println("Sum: " + sum);
    }

    private static void part2() throws Exception {
        final List<ListOrInteger> parsed = parse();
        final ListOrInteger dividerPacketStart = new ListOrInteger(List.of(new ListOrInteger(2)));
        final ListOrInteger dividerPacketEnd = new ListOrInteger(List.of(new ListOrInteger(6)));

        parsed.add(dividerPacketStart);
        parsed.add(dividerPacketEnd);

        Collections.sort(parsed, (left, right) -> {
            switch (ListOrInteger.compareTwo(left, right)) {
                case TRUE: return -1;
                case FALSE: return 1;
                case UNCLEAR: return 0;
            }
            throw new RuntimeException("This cannot happen");
        });

        final int indexOfDividerPacketStart = parsed.indexOf(dividerPacketStart) + 1;
        final int indexOfDividerPacketEnd = parsed.indexOf(dividerPacketEnd) + 1;

        System.out.println("Divider packets found at " + indexOfDividerPacketStart + " and " + indexOfDividerPacketEnd);
        System.out.printf("Decoder key: " + (indexOfDividerPacketStart * indexOfDividerPacketEnd));
    }



    private static List<ListOrInteger> parse() throws Exception{
        final List<ListOrInteger> parsed = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day13-step1.txt")));
        String read = reader.readLine();
        while (read != null) {
            if (read.isEmpty()) {
                // Do nothing
            } else {
                parsed.add(ListOrInteger.parse(read, 0).l);
            }
            read = reader.readLine();
        }
        return parsed;
    }

    public static class ListOrInteger {
        final List<ListOrInteger> list;
        final int integer;

        public ListOrInteger(final int i) {
            list = null;
            integer = i;
        }

        public ListOrInteger(final List<ListOrInteger> l) {
            list = l;
            integer = -1;
        }

        public static TriStateBool compareTwo(final ListOrInteger left, final ListOrInteger right) {
            // If both values are integers, the lower integer should come first.
            if (left.list == null && right.list == null) {
                if (left.integer < right.integer) {
                    return TRUE;
                } else if (left.integer == right.integer) {
                    // Otherwise, the inputs are the same integer; continue checking the next part of the input.
                    return UNCLEAR;
                } else {
                    return FALSE;
                }
            }

            // If both values are lists, compare the first value of each list, then the second value, and so on.
            else if (left.list != null && right.list != null) {
                final int shortestList = Math.min(left.list.size(), right.list.size());
                for(int i = 0; i < shortestList; i++) {
                    final TriStateBool result = compareTwo(left.list.get(i), right.list.get(i));
                    if (result != UNCLEAR) {
                        return result;
                    }
                }

                // If the left list runs out of items first, the inputs are in the right order.
                if (left.list.size() < right.list.size()) {
                    return TRUE;
                } else if (left.list.size() > right.list.size()) {
                    return FALSE;
                } else {
                    return UNCLEAR;
                }
            }

            // If exactly one value is an integer, convert the integer to a list which contains that integer
            // as its only value, then retry the comparison.
            else {
                if (left.list == null) {
                    return compareTwo(new ListOrInteger(List.of(new ListOrInteger(left.integer))), right);
                } else {
                    return compareTwo(left, new ListOrInteger(List.of(new ListOrInteger(right.integer))));
                }
            }
        }

        public static ParseResult parse(final String str, final int index) {
            char first = str.charAt(index);
            if (first == '[') {
                // I am a list
                int nextToken = index + 1;
                List<ListOrInteger> list = new ArrayList<>();
                while (str.charAt(nextToken) != ']') {
                    final ParseResult result = parse(str, nextToken);
                    list.add(result.l);
                    nextToken = result.newIndex;
                    if (str.charAt(nextToken) == ',') {
                        nextToken++;
                    }
                }
                return new ParseResult(new ListOrInteger(list), nextToken + 1);
            }
            if (first != '[' && first != ']' && first != ',') {
                // I am an integer.
                final int nextEndList = str.indexOf(']', index);
                final int nextComma = str.indexOf(',', index);
                final int end = Math.min(
                        nextEndList == -1 ? Integer.MAX_VALUE : nextEndList,
                        nextComma == -1 ? Integer.MAX_VALUE : nextComma);
                final String substring = str.substring(index, end);
                return new ParseResult(new ListOrInteger(Integer.parseInt(substring)), end);
            }

            throw new RuntimeException("Unexpected parse state: Current char " + str.charAt(index) + " From: " + str + " At: " + index);
        }

        public static record ParseResult(ListOrInteger l, int newIndex) {}
    }

    public enum TriStateBool {
        FALSE,
        TRUE,
        UNCLEAR
    }
}
