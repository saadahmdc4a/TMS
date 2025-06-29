import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.*;

public class TrafficGUI extends Application {
    private TrafficSimulator simulator;
    private CityGraph graph;
    private Pane canvas;

    private final Map<String, Circle> intersectionNodes = new HashMap<>();
    private final Map<String, double[]> nodePositions = new HashMap<>();
    private final List<Line> roadLines = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        graph = setupGraph(); // Your existing Main.java graph logic
        simulator = new TrafficSimulator(graph);

        canvas = new Pane();
        Scene scene = new Scene(canvas, 1000, 800);
        primaryStage.setTitle("Traffic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        drawIntersections(); // place circles and labels
        drawRoads();         // draw gray road lines behind circles

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            simulator.simulateTick();
            updateGUI();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void drawIntersections() {
        int radius = 15;
        double spacingX = 150;
        double spacingY = 120;

        int cols = 10; // 10x5 grid = 50 intersections
        int rows = 5;

        int id = 1;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                String name = "I" + id;
                double px = 80 + x * spacingX;
                double py = 80 + y * spacingY;

                Circle node = new Circle(px, py, radius, Color.RED);
                Text label = new Text(px - 10, py - 20, name);
                canvas.getChildren().addAll(node, label);

                intersectionNodes.put(name, node);
                nodePositions.put(name, new double[]{px, py}); // ✅ store coordinates
                id++;
            }
        }
    }

    private void drawRoads() {
        Set<String> drawn = new HashSet<>();

        for (String from : graph.getAllIntersections().keySet()) {
            Intersection fromIntersection = graph.getIntersection(from);
            if (fromIntersection == null) continue;

            double[] fromPos = nodePositions.get(from);
            if (fromPos == null) continue;

            for (String to : fromIntersection.getAllDirections()) {
                String edgeId = from + "->" + to;
                String reverseEdgeId = to + "->" + from;

                if (drawn.contains(edgeId) || drawn.contains(reverseEdgeId)) continue;

                double[] toPos = nodePositions.get(to);
                if (toPos == null) continue;

                Line road = new Line(fromPos[0], fromPos[1], toPos[0], toPos[1]);
                road.setStrokeWidth(2);
                road.setStroke(Color.GRAY);

                canvas.getChildren().add(0, road); // send behind circles
                roadLines.add(road);
                drawn.add(edgeId);
            }
        }
    }

    private void updateGUI() {
        for (Map.Entry<String, Circle> entry : intersectionNodes.entrySet()) {
            String name = entry.getKey();
            Circle node = entry.getValue();
            Intersection intersection = graph.getIntersection(name);
            if (intersection == null) continue;

            int total = 0;
            for (String dir : intersection.getAllDirections()) {
                total += intersection.getQueueSize(dir);
            }

            if (total == 0) {
                node.setFill(Color.LIGHTGREEN);
            } else if (total < 5) {
                node.setFill(Color.YELLOW);
            } else {
                node.setFill(Color.ORANGERED);
            }
        }
    }

    private CityGraph setupGraph() {
        CityGraph graph = new CityGraph();
        Random random = new Random();

        for (int i = 1; i <= 50; i++) {
            graph.addIntersection("I" + i);
        }

        for (int i = 1; i <= 50; i++) {
            String from = "I" + i;
            for (int j = i + 1; j <= Math.min(i + 3, 50); j++) {
                String to = "I" + j;
                int weight = random.nextInt(5) + 1;
                graph.addRoad(from, to, weight);
                graph.addRoad(to, from, weight);

                Intersection fromI = graph.getIntersection(from);
                Intersection toI = graph.getIntersection(to);
                if (fromI != null) fromI.addLane(to);
                if (toI != null) toI.addLane(from);
            }
        }

        return graph;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
