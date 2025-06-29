import java.util.*;

public class CityGraph {
    private final Map<String, Intersection> intersections;
    private final Map<String, List<Road>> adjacencyList;

    public CityGraph() {
        intersections = new HashMap<>();
        adjacencyList = new HashMap<>();

    }

    public Map<String, List<Road>> getAdjacencyList() {
        return adjacencyList;
    }

    // ✅ Safely add an intersection
    public void addIntersection(String name) {
        if (!intersections.containsKey(name)) {
            Intersection intersection = new Intersection(name);
            intersections.put(name, intersection);
            adjacencyList.put(name, new ArrayList<>());
        }
    }

    // ✅ Safely add a road between two intersections
    public void addRoad(String from, String to, int weight) {
        // Only add road if both intersections exist
        if (!intersections.containsKey(from) || !intersections.containsKey(to)) {
            System.err.println("❌ Cannot add road: " + from + " -> " + to + " (missing node)");
            return;
        }

        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>());
        adjacencyList.get(from).add(new Road(from, to, weight));
    }

    // ✅ Accessor for intersections
    public Intersection getIntersection(String name) {
        return intersections.get(name);
    }

    // ✅ Returns all intersections (safe)
    public Map<String, Intersection> getAllIntersections() {
        return intersections;
    }

    // ✅ Dijkstra’s algorithm for shortest path (by weight)
    public List<String> getShortestPath(String start, String end) {
        if (!intersections.containsKey(start) || !intersections.containsKey(end)) {
            return Collections.emptyList();
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String node : intersections.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);

        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue));
        pq.offer(new AbstractMap.SimpleEntry<>(start, 0));

        while (!pq.isEmpty()) {
            String current = pq.poll().getKey();
            if (visited.contains(current))
                continue;
            visited.add(current);

            if (current.equals(end))
                break;

            List<Road> neighbors = adjacencyList.getOrDefault(current, Collections.emptyList());
            for (Road road : neighbors) {
                String neighbor = road.getTo();
                if (neighbor == null || !intersections.containsKey(neighbor))
                    continue;

                int newDist = distances.get(current) + road.getWeight();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    pq.offer(new AbstractMap.SimpleEntry<>(neighbor, newDist));
                }
            }
        }

        // ✅ Reconstruct path
        List<String> path = new LinkedList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }

        return !path.isEmpty() && path.get(0).equals(start) ? path : Collections.emptyList();
    }
}
