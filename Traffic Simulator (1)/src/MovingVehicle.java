public class MovingVehicle {
    public Vehicle vehicle;
    public String from;
    public String to;
    public int remainingTime;

    public MovingVehicle(Vehicle vehicle, String from, String to, int travelTime) {
        this.vehicle = vehicle;
        this.from = from;
        this.to = to;
        this.remainingTime = travelTime;
    }
}
