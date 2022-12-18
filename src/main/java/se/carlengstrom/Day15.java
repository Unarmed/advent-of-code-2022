package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

    // Some things to note that aren't spelled out in the exercise but "shown" in the example:
    //
    // 1. Beacons count as places where a beacon could exist. (marked as B, instead of # and not counted)
    //
    // 2. Row "length" is determined by sensor and beacon coverage. The examples start showing X=-2 as the
    // lowest X, but in the final example, X=-4 is seen as one it is an impossible position too. This is a little
    // sneaky as the example works if one assumes that the area is bounded by the beacon/sensor position only
    // but the actual task fails.

    public static void main(String[] args) throws Exception {
        //part1();
        part2();
    }

    private static void part2() throws Exception {
        final List<SensorAndBeacon> sensorAndBeacons = parseSensorsAndBeacons();
        final int lowestX = sensorAndBeacons.stream()
                .map(sab -> Math.min(sab.beaconX, sab.sensorX) - sab.manhattanDistance)
                .min(Integer::compareTo)
                .get();
        final int highestX = sensorAndBeacons.stream()
                .map(sab -> Math.max(sab.beaconX, sab.sensorX) + sab.manhattanDistance)
                .max(Integer::compareTo)
                .get();

        System.out.println("Rows are of length: " + (highestX - lowestX) + "("+lowestX + ":" + highestX + ")");

        for (int i = 0; i <= 4000000; i++) {
            if (i % 1000 == 0) {
                System.out.println("Testing column: " + i);
            }
            for (int j = 0; j <= 4000000; j++) {
                final int x = i;
                final int y = j;
                final Optional<SensorAndBeacon> blocker = sensorAndBeacons.stream()
                        .filter(sab -> isImpossibleLocation(x, y, sab))
                        .findAny();

                // OK, blocked. For how long?
                if (blocker.isPresent()) {
                    final SensorAndBeacon sab = blocker.get();

                    // Skip until out of sensor range.
                    final int xDiff = Math.abs(sab.sensorX - x);
                    final int maxPush = sab.manhattanDistance - xDiff;
                    j = sab.sensorY + maxPush;

                } else {
                    System.out.println("Spot found at " + x + ", " + y);
                    System.out.println("Tuning frequency is: " + (x * 4000000L + y));
                    return;
                }
            }
        }
    }

    private static void part1() throws Exception {
        final List<SensorAndBeacon> sensorAndBeacons = parseSensorsAndBeacons();
        final int lowestX = sensorAndBeacons.stream()
                .map(sab -> Math.min(sab.beaconX, sab.sensorX) - sab.manhattanDistance)
                .min(Integer::compareTo)
                .get();
        final int highestX = sensorAndBeacons.stream()
                .map(sab -> Math.max(sab.beaconX, sab.sensorX) + sab.manhattanDistance)
                .max(Integer::compareTo)
                .get();

        System.out.println("Rows are of length: " + (highestX - lowestX) + "("+lowestX + ":" + highestX + ")");

        final int ROW_TO_CHECK = 2000000;
        int counter = 0;
        for (int i = lowestX; i <= highestX; i++) {
            final int x = i;
            final boolean isBlocked = sensorAndBeacons.stream()
                    .filter(sab -> isImpossibleLocation(x, ROW_TO_CHECK, sab))
                    .findAny()
                    .isPresent();

            // The task does not explicitly say this, but the example shows it.
            // If a spot is a beacon, that is not counted as a position where the beacon can't be.
            final boolean isBeacon = sensorAndBeacons.stream()
                    .filter(sab -> x == sab.beaconX() && ROW_TO_CHECK == sab.beaconY())
                    .findAny()
                    .isPresent();
            if (isBlocked && !isBeacon) {
                counter++;
            }
        }
        System.out.println("There are " + counter + " impossible locations in the row");
    }

    private static boolean isImpossibleLocation(final int x, final int y, final SensorAndBeacon sensorAndBeacon) {
        final int manhattanDistance = Math.abs(x - sensorAndBeacon.sensorX) + Math.abs(y - sensorAndBeacon.sensorY);
        return manhattanDistance <= sensorAndBeacon.manhattanDistance;
    }

    private static List<SensorAndBeacon> parseSensorsAndBeacons() throws Exception {
        final List<SensorAndBeacon> sensorsAndBeacons = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day15-step1.txt")));
        final String regex = "Sensor at x=([\\d-]+), y=([\\d-]+): closest beacon is at x=([\\d-]+), y=([\\d-]+)";
        final Pattern pattern = Pattern.compile(regex);
        String read = reader.readLine();
        while(read != null) {
            final Matcher matcher = pattern.matcher(read);
            if (matcher.matches()) {
                final int sensorX = Integer.parseInt(matcher.group(1));
                final int sensorY = Integer.parseInt(matcher.group(2));
                final int beaconX = Integer.parseInt(matcher.group(3));
                final int beaconY = Integer.parseInt(matcher.group(4));
                final int manhattanDistance = Math.abs(sensorX - beaconX) + Math.abs(sensorY - beaconY);
                sensorsAndBeacons.add(new SensorAndBeacon(sensorX, sensorY, beaconX, beaconY, manhattanDistance));
            } else {
                throw new RuntimeException("Failed match: " + read);
            }
            read = reader.readLine();
        }
        return sensorsAndBeacons;
    }

    public static record SensorAndBeacon(int sensorX, int sensorY, int beaconX, int beaconY, int manhattanDistance) {}
}
