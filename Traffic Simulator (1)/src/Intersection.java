import java.util.*;

public class Intersection {
    private final String name;
    private final Map<String, Queue<Vehicle>> directionQueues;
    private final PriorityQueue<Vehicle> emergencyQueue;
    private final Map<String, Boolean> trafficLightStatus;

    public Intersection(String name) {
        this.name = name;
        this.directionQueues = new HashMap<>();
        this.emergencyQueue = new PriorityQueue<>();
        this.trafficLightStatus = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addLane(String direction) {
        directionQueues.putIfAbsent(direction, new LinkedList<>());
        trafficLightStatus.putIfAbsent(direction, false);
    }

    public void enqueueVehicle(String direction, Vehicle vehicle) {
        addLane(direction); // auto-add if missing
        if (vehicle.getType() == Vehicle.Type.EMERGENCY) {
            emergencyQueue.offer(vehicle);
        } else {
            directionQueues.get(direction).offer(vehicle);
        }
    }

    public Vehicle dequeueVehicle(String direction) {
        if (!emergencyQueue.isEmpty())
            return emergencyQueue.poll();
        Queue<Vehicle> q = directionQueues.getOrDefault(direction, new LinkedList<>());
        return q.poll();
    }

    public boolean isGreen(String direction) {
        return trafficLightStatus.getOrDefault(direction, false);
    }

    public void setGreen(String direction) {
        trafficLightStatus.replaceAll((k, v) -> false);
        trafficLightStatus.put(direction, true);
    }

    public Collection<String> getAllDirections() {
        return directionQueues.keySet();
    }

    public int getQueueSize(String direction) {
        Queue<Vehicle> queue = directionQueues.get(direction);
        return (queue != null) ? queue.size() : 0;
    }

    public int getTotalQueueSize() {
        int normal = directionQueues.values().stream().mapToInt(Queue::size).sum();
        return normal + emergencyQueue.size();
    }

}
