import java.util.*;

public class StatsTracker {
    private final List<Long> waitTimes;
    private int totalTicks;

    public StatsTracker() {
        waitTimes = new ArrayList<>();
    }

    public void record(Vehicle vehicle) {
        waitTimes.add(vehicle.getWaitTime());
    }

    public double getAverageWaitTime() {
        return waitTimes.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    public double getEfficiencyScore() {
        return waitTimes.isEmpty() ? 100.0 : 10000.0 / getAverageWaitTime();
    }

public void setTotalTicks(int totalTicks) {
    this.totalTicks = totalTicks;
}
}
