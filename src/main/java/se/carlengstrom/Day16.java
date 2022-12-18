package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 {
    public static void main(String[] args) throws Exception{
        //part1();
        part2();
    }

    private static void part1() throws Exception{
        final HashMap<String, Node> graph = parseGraph();

        final int max = search2(graph, "AA", new HashSet<>(), 0, 0);
        System.out.println("Max flow is: " + max);
    }

    private static void part2() throws Exception {
        final HashMap<String, Node> graph = parseGraph();
        final Map<String, List<List<String>>> nodePaths = graph.values().stream()
                .filter(n -> n.flow > 0 || n.name.equals("AA"))
                .collect(Collectors.toMap(
                        n -> n.name,
                        n -> miniSearch(n.name, graph, new HashSet<>())));

        System.out.println("Precomputation complete");

        search3WithElephant(graph, nodePaths);
    }

    private static int search2(final Map<String, Node> graph, final String currentNode, final Set<String> openValves, int previousFlow, int iteration) {
        final List<List<String>> paths = miniSearch(currentNode, graph, openValves);

        final int flow = calculateFlow(graph, openValves);
        // Simplest thing to do is to just idle
        int val = previousFlow + (flow * (30 - iteration - 1));

        for(final List<String> path : paths) {
            final int stepsRequired = path.size(); // Path contains current so size is steps + one to turn on valve
            if (iteration + stepsRequired < 30) { // If there is still time

                // Go to this node and open it
                final String nextNode = path.get(path.size() - 1);
                // Adding current flow steps time, but last step will also include that node
                final int addedFlow = (flow * stepsRequired) + graph.get(nextNode).flow;
                final Set<String> newOpen = new HashSet<>(openValves);
                newOpen.add(nextNode);

                int pathValue = search2(graph, nextNode, newOpen, previousFlow + addedFlow, iteration + stepsRequired);
                if (pathValue > val) {
                    val = pathValue;
                }
            }
        }
        return val;
    }

    // search3WithElephant is called so because there was a searchWithElephant and a search2WithElephant and they
    // both didn't work for complexity reasons
    private static void search3WithElephant(
            final Map<String, Node> graph,
            final Map<String, List<List<String>>> nodePaths) {

        final Set<Path> completedPaths = new HashSet<>();
        final Queue<Path> workingPaths = new LinkedList<>();

        // Seed run
        for (List<String> path : nodePaths.get("AA")) {
            final Segment newSegment = new Segment(path);
            final Path newPath = new Path(List.of(newSegment), graph);
            completedPaths.add(newPath);
            workingPaths.add(newPath);
        }


        int counter = 0;
        while (!workingPaths.isEmpty()) {
            final Path poll = workingPaths.poll();

            for (List<String> path : nodePaths.get(poll.last)) {
                final String last = path.get(path.size() - 1);
                if (!poll.visited.contains(last)) {
                    final Segment newSegment = new Segment(path);
                    final Path newPath = poll.copyWith(newSegment, graph);
                    if (newPath.pathLength() <= 26) {
                        completedPaths.add(newPath);
                        workingPaths.add(newPath);
                        counter++;
                        if (counter % 1000 == 0) {
                            System.out.println("Generated " + counter + " paths");
                        }
                    }
                }
            }
        }

        System.out.println(completedPaths.size() + " path generated in total");

        System.out.println("Starting path matching");
        int matchesExpected = completedPaths.size() * completedPaths.size();
        int counter2 = 0;
        int val = 0;
        for (final Path p1 : completedPaths) {
            for (final Path p2 : completedPaths) {
                if (Collections.disjoint(p1.visited, p2.visited)) {
                    int value = p1.flow + p2.flow;
                    if (value > val) {
                        System.out.println("Found a better pair! Value: " + value);
                        System.out.println("Me: " + String.join(", ", p1.visited));
                        System.out.println("Elephant: " + String.join(", ", p2.visited));
                        val = value;
                    }
                }
                counter2++;
                if (counter2 % 10000000 == 0) {
                    System.out.println("Scanned " + counter2 + "/" + matchesExpected + " paths (" + ((double)counter2 / matchesExpected) + ")");
                }
            }
        }
    }

    public static class Path {
        public List<Segment> segments;
        public Set<String> visited;
        public String last;
        public int flow;

        public Path(final List<Segment> segments, final Map<String, Node> graph) {
            this.segments = segments;
            this.visited = segments.stream()
                    .map(l -> l.parts.get(l.parts.size() - 1))
                    .collect(Collectors.toSet());
            final Segment lastSegment = segments.get(segments.size() - 1);
            last = lastSegment.parts.get(lastSegment.parts.size() - 1);

            flow = 0;
            int position = 0;
            for (final Segment s : segments) {
                final String destination = s.parts.get(s.parts.size() - 1);
                final int destinationFlow = graph.get(destination).flow;
                position += s.parts.size();
                flow += (26 - position) * destinationFlow;
            }
        }

        public Path copyWith(final Segment newSegment, final Map<String, Node> graph) {
            final ArrayList<Segment> currentSegments = new ArrayList<>(this.segments);
            currentSegments.add(newSegment);
            return new Path(currentSegments, graph);
        }

        public int pathLength() {
            return segments.stream().map(s -> s.parts.size()).reduce(Integer::sum).orElse(0);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Path path = (Path) o;
            return segments.equals(path.segments);
        }

        @Override
        public int hashCode() {
            return Objects.hash(segments);
        }
    }
    private static record Segment (List<String> parts) {}

    private static List<List<String>> miniSearch(final String currentNode, final Map<String, Node> graph, final Set<String> openValves) {
        final PriorityQueue<List<String>> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a.size()));
        final Set<String> visited = new HashSet<>();

        queue.add(List.of(currentNode));

        final ArrayList<List<String>> res = new ArrayList<>();
        while (!queue.isEmpty()) {
            final List<String> poll = queue.poll();
            final String node = poll.get(poll.size() - 1);
            if (visited.contains(node))  {
                continue;
            }

            visited.add(node);

            // This is a possibility
            if (graph.get(node).flow > 0 && !openValves.contains(node)) {
                res.add(poll);
            }

            for(final String edge : graph.get(node).edges) {
                final List<String> newPath = new ArrayList<>(poll);
                newPath.add(edge);
                queue.add(newPath);
            }
        }
        return res;
    }

    private static int calculateFlow(final Map<String, Node> graph, final Set<String> openValves) {
        return openValves.stream()
                .map(v ->  graph.get(v).flow)
                .reduce(0, (a,b) -> a+b);
    }

    private static HashMap<String, Node> parseGraph() throws Exception {
        final String regex = "Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? (.*)";
        final Pattern pattern = Pattern.compile(regex);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day16-step1.txt")));

        final HashMap<String, Node> nodes = new HashMap<>();
        String read = reader.readLine();
        while (read != null) {
            final Matcher matcher = pattern.matcher(read);
            if (!matcher.matches()) {
                throw new RuntimeException("Unexpected string: " + read);
            }

            final String valve = matcher.group(1);
            final int flow = Integer.parseInt(matcher.group(2));
            final List<String> paths = Arrays.stream(matcher.group(3).split(","))
                    .map(s -> s.trim())
                    .collect(Collectors.toList());

            nodes.put(valve, new Node(valve, flow, paths));

            read = reader.readLine();
        }
        return nodes;
    }

    private static record Node (String name, int flow, List<String> edges) {}
}
