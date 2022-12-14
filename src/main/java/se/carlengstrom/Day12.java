package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Day12 {
    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final Map map = readMap();
        dijkstra(map.start, map.end, map);
    }

    private static void part2() throws Exception {
        final Map map = readMap();
        int shortest = Integer.MAX_VALUE;
        for (final Point p : map.lowPoints) {
            int dijkstra = dijkstra(p, map.end, map);
            if (dijkstra < shortest) {
                shortest = dijkstra;
            }
        }
        System.out.println("Best hiking trail has length: " + shortest);
    }

    private static int dijkstra(final Point start, final Point end, final Map map) {
        // Dijkstra
        final PriorityQueue<List<Point>> queue = new PriorityQueue<>(Comparator.comparingInt(List::size));
        final Set<Point> visited = new HashSet<>();
        final List<Point> startPath = List.of(start);
        queue.add(startPath);

        while (!queue.isEmpty()) {
            final List<Point> path = queue.poll();
            final Point last = path.get(path.size() - 1);
            if (last.equals(end)) {
                // -1 here because my path includes first node, solution does not count that as a step
                System.out.println("Found path to end! Took " + (path.size() - 1)  + " steps!");
                return (path.size() - 1);
            }

            if (visited.contains(last)) {
                continue;
            }
            visited.add(last);

            // Go left?
            if (last.x > 0 && map.data[last.x-1][last.y] <= map.data[last.x][last.y] + 1) {
                final ArrayList<Point> newPath = new ArrayList<>(path);
                newPath.add(new Point(last.x-1, last.y));
                queue.add(newPath);
            }

            // Go right?
            if (last.x < map.width - 1 && map.data[last.x+1][last.y] <= map.data[last.x][last.y] + 1) {
                final ArrayList<Point> newPath = new ArrayList<>(path);
                newPath.add(new Point(last.x+1, last.y));
                queue.add(newPath);
            }

            // Go up?
            if (last.y > 0 && map.data[last.x][last.y-1] <= map.data[last.x][last.y] + 1) {
                final ArrayList<Point> newPath = new ArrayList<>(path);
                newPath.add(new Point(last.x, last.y-1));
                queue.add(newPath);
            }

            // Go down?
            if (last.y < map.height - 1 && map.data[last.x][last.y+1] <= map.data[last.x][last.y] + 1) {
                final ArrayList<Point> newPath = new ArrayList<>(path);
                newPath.add(new Point(last.x, last.y+1));
                queue.add(newPath);
            }
        }

        System.out.println("No path found for starting point " + start);
        return Integer.MAX_VALUE;
    }

    private static Map readMap() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day12-step1.txt")));
        String read = reader.readLine();
        final int width = read.length();
        final ArrayList<String> lines = new ArrayList<>();
        while(read != null) {
            lines.add(read);
            read = reader.readLine();
        }

        final int height = lines.size();
        final int[][] data = new int[width][height];
        final List<Point> lowPoints = new ArrayList<>();
        Point start = null;
        Point end = null;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                final char c = lines.get(y).charAt(x);
                if (c == 'S') {
                    start = new Point(x,y);
                    data[x][y] = 'a';
                    lowPoints.add(new Point(x,y));
                } else if (c == 'E') {
                    end = new Point(x,y);
                    data[x][y] = 'z';
                } else {
                    data[x][y] = c;
                    if (c == 'a') {
                        lowPoints.add(new Point(x,y));
                    }
                }
            }
        }

        if (start == null || end == null) {
            throw new RuntimeException("Unable to find start or end!");
        }
        return new Map(data, start, end, width, height, lowPoints);
    }

    private static record Point(int x, int y) {}
    private static record Map(int[][] data, Point start, Point end, int width, int height, List<Point> lowPoints) {}
}
