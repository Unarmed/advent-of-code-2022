package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Day8 {

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part1() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day8-step1.txt")));
        String read = reader.readLine();
        final int width = read.length();
        final ArrayList<String> readLines = new ArrayList<>();
        while (read != null) {
            readLines.add(read);
            read = reader.readLine();
        }

        final int height = readLines.size();
        final int[][] boxes = new int[width][height];
        final boolean[][] visible = new boolean[width][height];
        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {
                boxes[x][y] = Integer.parseInt(Character.toString(readLines.get(y).charAt(x)));
                visible[x][y] = false;
            }
        }

        // Visible from left
        for (int y = 0; y < height ; y++) {
            int maxSeen = -1;
            for (int x = 0; x < width ; x++) {
                if (boxes[x][y] > maxSeen) {
                  visible[x][y] = true;
                  maxSeen = boxes[x][y];
                }
            }
        }

        // Visible from right
        for (int y = 0; y < height ; y++) {
            int maxSeen = -1;
            for (int x = width -1 ; x >= 0 ; x--) {
                if (boxes[x][y] > maxSeen) {
                    visible[x][y] = true;
                    maxSeen = boxes[x][y];
                }
            }
        }

        // Visible from top
        for (int x = 0; x < width ; x++) {
            int maxSeen = -1;
            for (int y = 0; y < height ; y++) {
                if (boxes[x][y] > maxSeen) {
                    visible[x][y] = true;
                    maxSeen = boxes[x][y];
                }
            }
        }

        // Visible from bottom
        for (int x = 0; x < width ; x++) {
            int maxSeen = -1;
            for (int y = height - 1; y >= 0 ; y--) {
                if (boxes[x][y] > maxSeen) {
                    visible[x][y] = true;
                    maxSeen = boxes[x][y];
                    }
            }
        }

        // Count
        int count = 0;
        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {
                if (visible[x][y]) {
                    count++;
                }
            }
        }
        System.out.println("Visible trees: " + count);
    }

    private static void part2() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day8-step1.txt")));
        String read = reader.readLine();
        final int width = read.length();
        final ArrayList<String> readLines = new ArrayList<>();
        while (read != null) {
            readLines.add(read);
            read = reader.readLine();
        }

        final int height = readLines.size();
        final int[][] boxes = new int[width][height];

        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {
                boxes[x][y] = Integer.parseInt(Character.toString(readLines.get(y).charAt(x)));
            }
        }

        // The complexity of this is absurd :(
        int mostBeautiful = 0;
        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height ; y++) {

                int xUp = x + 1;
                while (xUp != width && boxes[xUp][y] < boxes[x][y]) {
                    xUp++;
                }
                // Don't count the edge if hit
                if (xUp == width) {
                    xUp--;
                }
                int distanceXup = xUp - x;

                int xDown = x - 1;
                while (xDown != -1 && boxes[xDown][y] < boxes[x][y]) {
                    xDown--;
                }
                // Don't count the edge if hit
                if (xDown == -1) {
                    xDown++;
                }
                int distanceXDown = x - xDown;

                int yUp = y + 1;
                while (yUp != height && boxes[x][yUp] < boxes[x][y]) {
                    yUp++;
                }
                // Don't count the edge if hit
                if (yUp == height) {
                    yUp--;
                }
                int distanceYup = yUp - y;

                int yDown = y - 1;
                while (yDown != -1 && boxes[x][yDown] < boxes[x][y]) {
                    yDown--;
                }
                // Don't count the edge if hit
                if (yDown == -1) {
                    yDown++;
                }
                int distanceYDown = y - yDown;

                int beauty = distanceXDown * distanceXup * distanceYDown * distanceYup;
                if (beauty > mostBeautiful) {
                    mostBeautiful = beauty;
                }
            }
        }


        System.out.println("Most beautiful: " + mostBeautiful);
    }
}
