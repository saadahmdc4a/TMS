public class Vehicle implements Comparable<Vehicle> {
    public enum Type { NORMAL, EMERGENCY }

    private static int nextId = 1;
    private final int id;
    private final Type type;
    private final long arrivalTime;
    private long startTime;
    private long endTime;

    public Vehicle(Type type, long arrivalTime) {
        this.id = nextId++;
        this.type = type;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getWaitTime() {
        return endTime > arrivalTime ? endTime - arrivalTime : 0;
    }

    @Override
    public int compareTo(Vehicle other) {
        // Emergency vehicles have higher priority
        if (this.type == Type.EMERGENCY && other.type == Type.NORMAL) return -1;
        if (this.type == Type.NORMAL && other.type == Type.EMERGENCY) return 1;
        return Long.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return "Vehicle " + id + " (" + type + ")";
    }
}
