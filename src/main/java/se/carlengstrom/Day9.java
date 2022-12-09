package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Day9 {

    public static void main(String[] args) throws Exception{
        // part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day9-step1.txt")));
        Point head = new Point(0,0);
        Point tail = new Point(0, 0);
        final Set<Point> visited = new HashSet<>();
        visited.add(new Point(0,0));

        String read = reader.readLine();
        while (read != null) {
            final char action = read.charAt(0);
            final int times = Integer.parseInt(read.substring(2));
            for (int i = 0; i < times; i++) {
                final Point newHead;
                switch (action) {
                    case 'U':
                        newHead = new Point(head.x, head.y + 1);
                        break;
                    case 'D':
                        newHead = new Point(head.x, head.y - 1);
                        break;
                    case 'L':
                        newHead = new Point(head.x - 1, head.y);
                        break;
                    case 'R':
                        newHead = new Point(head.x + 1, head.y);
                        break;
                    default:
                        throw new RuntimeException("Unexpected action: " + action);
                }

                if (!isCloseEnough(newHead, tail)) {
                    tail = new Point(head.x, head.y);
                    visited.add(tail);
                }
                head = newHead;
            }
            read = reader.readLine();
        }
        System.out.println("Visited: " + visited.size());
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day9-step1.txt")));
        final List<Point> rope = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rope.add(new Point(0,0));
        }
        final Set<Point> visited = new HashSet<>();
        visited.add(new Point(0,0));

        String read = reader.readLine();
        while (read != null) {
            final char action = read.charAt(0);
            final int times = Integer.parseInt(read.substring(2));
            for (int i = 0; i < times; i++) {
                switch (action) {
                    case 'U':
                         visited.add(wiggleRope(rope, new Point(rope.get(0).x, rope.get(0).y + 1)));
                        break;
                    case 'D':
                        visited.add(wiggleRope(rope, new Point(rope.get(0).x, rope.get(0).y - 1)));
                        break;
                    case 'L':
                        visited.add(wiggleRope(rope, new Point(rope.get(0).x - 1, rope.get(0).y)));
                        break;
                    case 'R':
                        visited.add(wiggleRope(rope, new Point(rope.get(0).x + 1, rope.get(0).y)));
                        break;
                    default:
                        throw new RuntimeException("Unexpected action: " + action);
                }
            }
            read = reader.readLine();
        }
        System.out.println("Visited: " + visited.size());
    }

    public static Point wiggleRope(final List<Point> rope, final Point newHead) {
        rope.remove(0);
        rope.add(0, newHead);
        for(int i = 1; i < 10; i++) {
            if (!isCloseEnough(rope.get(i-1), rope.get(i))) {
                // The heuristic of part one that the tail always assumes the same position as the head previously
                // had breaks down here, because the "head" might have moved diagonally.
                // New rule: Move tail one step in all lagging dimensions.
                final Point head = rope.get(i-1);
                final Point tail = rope.get(i);
                final Point newTail = new Point(
                    tail.x + Integer.signum(head.x - tail.x),
                    tail.y + Integer.signum(head.y - tail.y));
                rope.remove(i);
                rope.add(i, newTail);
            } else {
                break;
            }
        }

        return rope.get(9);
    }

    public static boolean isSideWaysOf(Point head, Point tail) {
        return head.x == tail.x || head.y == tail.y;
    }

    public static int manhattanDistance(Point head, Point tail) {
        return Math.abs(head.x - tail.x) + Math.abs(head.y - tail.y);
    }

    public static boolean isTailDirectlyAdjacent(Point head, Point tail) {
        return manhattanDistance(head, tail) == 1;
    }

    public static boolean isTailDiagonal(Point head, Point tail) {
        return manhattanDistance(head, tail) == 2 && head.x != tail.x && head.y != tail.y;
    }

    public static boolean isCloseEnough(Point head, Point tail) {
        return head.equals(tail) || isTailDirectlyAdjacent(head, tail) || isTailDiagonal(head, tail);
    }

    private record Point(int x, int y) {}
}
