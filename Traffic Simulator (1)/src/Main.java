import java.util.Random;

public class Main {
    public static void main(String[] args) {
        CityGraph graph = new CityGraph();
        Random random = new Random();

        // ✅ STEP 1: Create all intersections first
        for (int i = 1; i <= 50; i++) {
            String name = "I" + i;
            graph.addIntersection(name);
        }

        // ✅ STEP 2: Create roads and safely add lanes
        for (int i = 1; i <= 50; i++) {
            String from = "I" + i;
            for (int j = i + 1; j <= Math.min(i + 3, 50); j++) {
                String to = "I" + j;
                int weight = random.nextInt(5) + 1;

                // Only add road if both nodes exist
                if (graph.getIntersection(from) != null && graph.getIntersection(to) != null) {
                    graph.addRoad(from, to, weight);
                    graph.addRoad(to, from, weight);

                    // Safely add lanes
                    Intersection fromI = graph.getIntersection(from);
                    Intersection toI = graph.getIntersection(to);
                    if (fromI != null) fromI.addLane(to);
                    if (toI != null) toI.addLane(from);
                }
            }
        }

        // ✅ STEP 3: Run simulation
        TrafficSimulator simulator = new TrafficSimulator(graph);
        for (int tick = 0; tick < 500; tick++) {
            for (int i = 0; i < 20; i++) {
                simulator.addRandomVehicle();
            }
            simulator.simulateTick();
        }

        simulator.printStats();
    }
}
