import java.util.*;

public class TrafficSimulator {
    private static final int CONGESTION_THRESHOLD = 5;

    private final CityGraph cityGraph;
    private final StatsTracker statsTracker;
    private final Random random;
    private long currentTime;
    private final List<Vehicle> activeVehicles = new ArrayList<>();

    public TrafficSimulator(CityGraph graph) {
        this.cityGraph = graph;
        this.statsTracker = new StatsTracker();
        this.random = new Random();
        this.currentTime = 0;
    }

    public void simulateTick() {
        currentTime++;

        Collection<Intersection> intersections = cityGraph.getAllIntersections().values();
        if (intersections == null) return;

        for (Intersection intersection : intersections) {
            for (String dir : intersection.getAllDirections()) {
                if (intersection.isGreen(dir)) {
                    Vehicle v = intersection.dequeueVehicle(dir);
                    if (v != null) {
                        v.setEndTime(currentTime);
                        statsTracker.record(v);
                        System.out.println("[Tick " + currentTime + "] Cleared: " + v + " from " + intersection.getName() + " -> " + dir);
                    }
                }
            }
            rotateGreen(intersection);
        }

        if (random.nextDouble() < 0.3) {
            addRandomVehicle();
        }
    }

    private void rotateGreen(Intersection i) {
        List<String> dirs = new ArrayList<>(i.getAllDirections());
        if (!dirs.isEmpty()) {
            i.setGreen(dirs.get(random.nextInt(dirs.size())));
        }
    }

    public void addRandomVehicle() {
        Map<String, Intersection> all = cityGraph.getAllIntersections();
        if (all == null || all.size() < 2) return;

        List<String> nodes = new ArrayList<>(all.keySet());
        String start, end;

        do {
            start = nodes.get(random.nextInt(nodes.size()));
            end = nodes.get(random.nextInt(nodes.size()));
        } while (start.equals(end));

        List<String> path = cityGraph.getShortestPath(start, end);
        if (path.size() >= 2) {
            String next = path.get(1);
            Intersection startIntersection = cityGraph.getIntersection(start);

            // ✅ Check congestion and reroute
            if (startIntersection != null && startIntersection.getQueueSize(next) >= CONGESTION_THRESHOLD) {
                List<String> newPath = getAlternatePath(start, end, next);
                if (newPath.size() >= 2) {
                    path = newPath;
                    next = path.get(1); // update to rerouted path
                }
            }

            Vehicle.Type type = random.nextDouble() < 0.1 ? Vehicle.Type.EMERGENCY : Vehicle.Type.NORMAL;
            Vehicle v = new Vehicle(type, currentTime);

            if (startIntersection != null) {
                startIntersection.enqueueVehicle(next, v);
                activeVehicles.add(v);
                System.out.println("[Tick " + currentTime + "] Added: " + v + " from " + start + " -> " + next +
                        (path.size() > 2 ? " (rerouted)" : ""));
            }
        } else {
            System.out.println("[Tick " + currentTime + "] No path from " + start + " to " + end);
        }
    }

    private List<String> getAlternatePath(String start, String end, String avoidNode) {
        Map<String, List<Road>> graphMap = cityGraph.getAdjacencyList();

        List<Road> original = new ArrayList<>(graphMap.getOrDefault(start, new ArrayList<>()));
        List<Road> filtered = new ArrayList<>();

        for (Road road : original) {
            if (!road.getTo().equals(avoidNode)) {
                filtered.add(road);
            }
        }

        // Temporarily update adjacency
        graphMap.put(start, filtered);
        List<String> newPath = cityGraph.getShortestPath(start, end);

        // Restore original roads
        graphMap.put(start, original);
        return newPath;
    }

    public void printStats() {
        System.out.println("\n--- Simulation Complete ---");
        System.out.println("Average Wait Time: " + statsTracker.getAverageWaitTime());
        System.out.println("Traffic Flow Efficiency: " + statsTracker.getEfficiencyScore());
    }
}
