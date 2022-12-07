package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class Day7 {
    public static void main(String[] args) throws Exception {
        part1and2();
    }

    private static void part1and2() throws Exception{
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day7-step1.txt")));
        String read = reader.readLine();
        final HashMap<String, FileOrDirectory> contents = new HashMap<>();
        Stack<String> currentPath = new Stack<>();
        FileOrDirectory current = null;
        contents.put(toPath(currentPath), new FileOrDirectory(0, new ArrayList<>()));
        while (read != null) {
            if (read.startsWith("$ cd")) {
                if (read.equals("$ cd /")) {
                    currentPath = new Stack<>();
                    current = contents.get(toPath(currentPath));
                } else if (read.equals("$ cd ..")) {
                    currentPath.pop();
                    current = contents.get(toPath(currentPath));
                } else {
                    final String newDir = read.substring(5);
                    currentPath.push(newDir);
                    // ComputeIfAbsent here in case we reenter same dir
                    FileOrDirectory newFoD = contents.computeIfAbsent(toPath(currentPath), (s) -> new FileOrDirectory(0, new ArrayList<>()));
                    current.containingDirs.add(newFoD);
                    current = newFoD;
                }

            } else if (read.equals("$ ls")) {
                // Ignore
            } else if (read.startsWith("dir")) {
                // Ignore, we'll enter via cd later
            } else {
                final String[] parts = read.split(" ");
                final long size = Long.parseLong(parts[0]);
                current.ownSize += size;
            }

            read = reader.readLine();
        }

        long sum = 0;
        for (Map.Entry<String, FileOrDirectory> e : contents.entrySet()) {
            long dirSize = e.getValue().getTotalSize();
            if (dirSize <= 100000) {
                sum += dirSize;
            }
        }
        System.out.println("Total sum: " + sum);

        final Map<String, Long> dirsAndSizes = contents.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getTotalSize()));
        final long maxSpace = 70000000;
        final long slashSpace = contents.get("").getTotalSize();
        final long availableSpace = maxSpace - slashSpace;
        final long spaceNeededToFree = 30000000 - availableSpace;

        System.out.println("/ is using " + slashSpace + ". Space available: " + availableSpace);
        System.out.println("Need to free: "  + spaceNeededToFree);

        final Map.Entry<String, Long> smallestToDelete = dirsAndSizes.entrySet()
                .stream()
                .filter(e -> e.getValue() >= spaceNeededToFree)
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .findFirst()
                .get();

        System.out.println("Smallest possible match is: " + smallestToDelete.getKey() + " with size " + smallestToDelete.getValue());
    }

    private static String toPath(final Stack<String> stack) {
        return stack.stream()
                .reduce("", (a,b) -> a + "/" + b);
    }

    private static class FileOrDirectory {
        public long ownSize;
        public final List<FileOrDirectory> containingDirs;

        public FileOrDirectory(final long ownSize, final List<FileOrDirectory> containingDirs) {
            this.ownSize = ownSize;
            this.containingDirs = containingDirs;
        }

        public long getTotalSize() {
            return ownSize + containingDirs.stream()
                    .map(d -> d.getTotalSize())
                    .reduce(Long::sum)
                    .orElse(0L);
        }
    }
}
