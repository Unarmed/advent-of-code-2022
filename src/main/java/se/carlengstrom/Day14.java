package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day14 {
    public static void main(String[] args) throws Exception {
        part1(true);
    }

    private static void part1(final boolean addFloor) throws Exception {
        final List<Line> lines = parseMap();
        final Set<Point> sand = new HashSet<>();
        // The "lowest" point is the highest, sand falls from 0 to 1, to 2, etc
        final int lowestPoint = lines.stream()
                .map(l -> l.lowestY())
                .max(Integer::compareTo)
                .get();

        if (addFloor) {
            lines.add(new InfiniteFloor(lowestPoint + 2));
        }

        boolean sandDropping = true;
        int sandsDropped = 0;
        while (sandDropping) {
            Point sandPos = new Point(500,0);
            if (isBlocked(sandPos, lines, sand)) {
                System.out.println("Sand blocked from dropping!");
                System.out.println("This took " + sandsDropped + " sand drops");
                sandDropping = false;
            }

            if (sandsDropped > 0 && sandsDropped % 100 == 0) {
                System.out.println("Dropping sand: " + sandsDropped);
            }

            boolean sandAtRest = false;
            while (!sandAtRest) {
                if (!addFloor && sandPos.y + 1 > lowestPoint) {
                    System.out.println("Sand is falling to the abyss at: " + sandPos);
                    System.out.println("This took " + sandsDropped + " sand drops");
                    sandAtRest = true;
                    sandDropping = false;
                }

                final Point down = new Point(sandPos.x, sandPos.y + 1);
                if (isBlocked(down, lines, sand)) {
                    final Point diagonalLeft = new Point(sandPos.x -1, sandPos.y + 1);
                    if (isBlocked(diagonalLeft, lines, sand)) {
                        final Point diagonalRight = new Point(sandPos.x + 1, sandPos.y + 1);
                        if (isBlocked(diagonalRight, lines, sand)) {
                            sand.add(sandPos);
                            sandsDropped++;
                            sandAtRest = true;
                        } else {
                            sandPos = diagonalRight;
                        }
                    } else {
                        sandPos = diagonalLeft;
                    }
                } else {
                    sandPos = down;
                }
            }
        }
    }

    private static boolean isBlocked(final Point p, final List<Line> lines, final Set<Point> sand) {
        if (sand.contains(p)) {
            return true;
        }

        if (lines.stream().filter(l -> l.contains(p)).findAny().isPresent()) {
            return true;
        }

        return false;
    }

    private static List<Line> parseMap() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day14-step1.txt")));
        String read = reader.readLine();
        final List<Line> lines = new ArrayList<>();
        while (read != null) {
            final String[] parts = read.split(" -> ");
            Point last = Point.fromString(parts[0]);
            for(int i = 1; i < parts.length; i++) {
                final Point current = Point.fromString(parts[i]);
                if (current.x == last.x) {
                    lines.add(new VerticalLine(current.x, last.y, current.y));
                } else {
                    lines.add(new HorizontalLine(current.x, last.x, current.y));
                }
                last = current;
            }

            read = reader.readLine();
        }
        return lines;
    }

    private static abstract class Line {
        public abstract boolean contains(final Point p);
        public abstract int lowestY();
    }

    private static class VerticalLine extends Line {
        private final int x;
        private final int lowY;
        private final int highY;

        public VerticalLine(final int x, final int y1, final int y2) {
            this.x = x;
            if (y1 < y2) {
                this.lowY = y1;
                this.highY = y2;
            } else {
                this.highY = y1;
                this.lowY = y2;
            }
        }

        @Override
        public boolean contains(final Point p) {
            return p.x == x && p.y >= lowY && p.y <= highY;
        }

        @Override
        public int lowestY() {
            return lowY;
        }
    }

    private static class HorizontalLine extends Line {
        private final int lowX;
        private final int highX;
        private final int y;

        public HorizontalLine(final int x1, final int x2, final int y) {
            this.y = y;
            if (x1 < x2) {
                this.lowX = x1;
                this.highX = x2;
            } else {
                this.highX = x1;
                this.lowX = x2;
            }
        }

        @Override
        public boolean contains(final Point p) {
            return p.y == y && p.x >= lowX && p.x <= highX;
        }

        @Override
        public int lowestY() {
            return y;
        }
    }

    private static class InfiniteFloor extends Line {
        private final int y;

        public InfiniteFloor(final int y) {
            this.y = y;
        }

        @Override
        public boolean contains(final Point p) {
            return p.y == y;
        }

        @Override
        public int lowestY() {
            return y;
        }
    }

    private static record Point(int x, int y) {
        public static Point fromString(final String str) {
            String[] parts = str.split(",");
            return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }
}
