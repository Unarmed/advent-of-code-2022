package se.carlengstrom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 {
    public static void main(String[] args) throws Exception {
        part1();
        part2();
    }

    private static void part1() throws Exception {
        monkeyBusiness(20, 3);
    }

    private static void part2() throws Exception {
        //monkeyBusiness(20, BigInteger.valueOf(3));
        //monkeyBusiness2(20, BigInteger.valueOf(3));
        monkeyBusiness3(10000);
    }

    // Note(eng): This works fine with regular int for part1. For part 2 it doesn't work, becuase of overflows, even
    // with long. I rewrote it with BigInteger, but that had performance issues.
    private static void monkeyBusiness(int rounds, int worryDivisor) throws Exception {
        final ArrayList<Monkey> monkeys = parseMonkeys();
        final Map<Integer, Long> monkeyCounter = new HashMap<>();

        for(int i = 0; i < rounds; i++) {
            for (int m = 0; m < monkeys.size(); m++) {
                final Monkey currentMonkey = monkeys.get(m);
                while (!currentMonkey.items.isEmpty()) {
                    final long item = currentMonkey.items.remove(0);
                    // Monkey inspects, which increases worry
                    final long inspectionWorry = currentMonkey.operation.apply(item);
                    // Item unharmed, which decreases worry
                    final long newWorry = inspectionWorry / worryDivisor;
                    // Increase inspection counter
                    monkeyCounter.put(currentMonkey.id, monkeyCounter.getOrDefault(currentMonkey.id, 0L) + 1);
                    // Throw
                    if (currentMonkey.test.test(newWorry)) {
                        monkeys.get(currentMonkey.ifTrueTarget).items.add(newWorry);
                    } else {
                        monkeys.get(currentMonkey.ifFalseTarget).items.add(newWorry);
                    }
                }
            }
            if (i > 0 && i % 100 == 0) {
                System.out.println("Round: " + i + " complete");
            }
        }

        final long monkeyBusiness = monkeyCounter.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(2)
                .map(e -> {
                    System.out.println("Top monkey: " + e.getKey() + " with " + e.getValue() + " inspections");
                    return e;
                })
                .map(e -> e.getValue())
                .reduce((a, b) -> a * b)
                .get();

        System.out.println("Monkey business: " + monkeyBusiness);
    }

    // There used to be a monkeyBusiness2 method here that followed each packet and attempted to find cycles.
    // Since there were no cycles, it had performance issues like part 1.

    // In this version, I keep track of the modulo instead of the number. This is a little finnicky as it has to
    // be tracked per monkey. But it's fast. It also assumes that (x mod y)^2 == x^2 mod y which I really can't
    // prove is the case, but it worked for some examples, and it delivers the right result for my input.
    private static void monkeyBusiness3(int rounds) throws Exception {
        final ArrayList<Monkey> monkeys = parseMonkeys();
        final Map<Integer, Long> monkeyCounter = new HashMap<>();

        ArrayList<ArrayList<Item>> items = new ArrayList<>();
        for (int m = 0; m < monkeys.size(); m++) {
            items.add(new ArrayList<>());
            for (int i = 0; i < monkeys.get(m).items.size(); i++) {
                items.get(m).add(new Item(monkeys.get(m).items.get(i), monkeys));
            }
        }

        for(int i = 0; i < rounds; i++) {
            for (int m = 0; m < monkeys.size(); m++) {
                final Monkey currentMonkey = monkeys.get(m);
                final ArrayList<Item> monkeyItems = items.get(m);
                while (!monkeyItems.isEmpty()) {
                    final Item item = monkeyItems.remove(0);
                    // Monkey inspects, which increases worry
                    final Item inspectionWorry = currentMonkey.itemOperation.apply(item);
                    // Increase inspection counter
                    monkeyCounter.put(currentMonkey.id, monkeyCounter.getOrDefault(currentMonkey.id, 0L) + 1);
                    // Throw
                    if (inspectionWorry.test(m)) {
                        items.get(currentMonkey.ifTrueTarget).add(inspectionWorry);
                    } else {
                        items.get(currentMonkey.ifFalseTarget).add(inspectionWorry);
                    }
                }
            }
            if (i > 0 && i % 100 == 0) {
                System.out.println("Round: " + i + " complete");
            }
        }

        final long monkeyBusiness = monkeyCounter.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(2)
                .map(e -> {
                    System.out.println("Top monkey: " + e.getKey() + " with " + e.getValue() + " inspections");
                    return e;
                })
                .map(e -> e.getValue())
                .reduce((a, b) -> a * b)
                .get();

        System.out.println("Monkey business: " + monkeyBusiness);
    }

    private static ArrayList<Monkey> parseMonkeys() throws Exception {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input/day11-step1.txt")));
        final ArrayList<Monkey> monkies = new ArrayList<>();

        while (true) {
            String read = reader.readLine();
            final int id = Integer.parseInt(read.substring("Monkey ".length(), read.length()-1));

            read = reader.readLine();
            // Forcing ArrayList here because I want to be able to mutate it later
            final ArrayList<Long> items = Arrays.stream(read.substring("  Starting items: ".length())
                    .split(","))
                    .map(str -> Long.parseLong(str.trim()))
                    .collect(Collectors.toCollection(ArrayList::new));

            read = reader.readLine();
            final String op = read.substring("  Operation: new = ".length());
            final Function<Long, Long> operation;
            final Function<Item,Item> itemOperation;
            if (op.equals("old + old")) {
                operation = (old) -> old + old;
                itemOperation = (item) -> item.multiply(2);
            } else if (op.equals("old * old")) {
                operation = (old) -> old * old;
                itemOperation = (item) -> item.square();
            } else if (op.startsWith("old * ")) {
                long value = Long.parseLong(op.substring("old * ".length()));
                operation = (old) -> old * value;
                itemOperation = (item) -> item.multiply(value);
            } else if (op.startsWith("old + ")) {
                long value = Long.parseLong(op.substring("old + ".length()));
                operation = (old) -> old + value;
                itemOperation = (item) -> item.add(value);
            } else {
                throw new RuntimeException("Unexpected op: " + op);
            }

            read = reader.readLine();
            final long divisor = Long.parseLong(read.substring("  Test: divisible by ".length()));
            final Function<Long, Long> testFunc = (value) -> value % divisor;
            final Predicate<Long> test = (value) -> testFunc.apply(value) == 0;



            read = reader.readLine();
            final int ifTrueTarget = Integer.parseInt(read.substring("    If true: throw to monkey ".length()));

            read = reader.readLine();
            final int ifFalseTarget = Integer.parseInt(read.substring("    If false: throw to monkey ".length()));

            final Monkey monkey = new Monkey(id, items, divisor, operation, itemOperation, testFunc, test, ifTrueTarget, ifFalseTarget);
            monkies.add(monkey);

            read = reader.readLine();
            if(read == null) {
                break;
            }
        }
        return monkies;
    }

    private static record Monkey (
            int id,
            ArrayList<Long> items,
            long divisor,
            Function<Long, Long> operation,
            Function<Item, Item> itemOperation,
            Function<Long, Long> testFunc,
            Predicate<Long> test,
            int ifTrueTarget,
            int ifFalseTarget) {}

    private static class Item {
        private final List<Monkey> monkeys;
        ArrayList<Long> values;

        public Item(final long value, final List<Monkey> monkeys) {
            this.monkeys = monkeys;
            values = monkeys.stream()
                    .map(m -> value % m.divisor)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        public Item multiply(long value) {
            values = IntStream.range(0, monkeys.size())
                    .mapToLong(i -> (values.get(i) * value) % monkeys.get(i).divisor)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
            return this;
        }

        public Item square() {
            values = IntStream.range(0, monkeys.size())
                    .mapToLong(i -> (values.get(i) * values.get(i)) % monkeys.get(i).divisor)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
            return this;
        }

        public Item add(long value) {
            values = IntStream.range(0, monkeys.size())
                    .mapToLong(i -> (values.get(i) + value) % monkeys.get(i).divisor)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
            return this;
        }

        public boolean test(final int monkeyIndex) {
            return values.get(monkeyIndex) == 0;
        }
    }
}
