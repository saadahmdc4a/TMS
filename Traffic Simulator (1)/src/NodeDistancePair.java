public class NodeDistancePair implements Comparable<NodeDistancePair> {
    public String node;
    public int distance;

    public NodeDistancePair(String node, int distance) {
        this.node = node;
        this.distance = distance;
    }

    @Override
    public int compareTo(NodeDistancePair other) {
        return Integer.compare(this.distance, other.distance);
    }
}
