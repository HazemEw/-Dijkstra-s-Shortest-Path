package Dijkstra;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class HelloApplication extends Application {
    public static Graph graph = new Graph();
    public static Map<String, Vertex> vertices = new HashMap<>();

    public static LinkedList <Line> linePath = new LinkedList<>();
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        InputStream stream = new FileInputStream("src\\resources\\Map.jpg");
        Image image = new Image(stream, 1150, 765, false, false);
        ImageView WorldMap = new ImageView();
        WorldMap.setImage(image);
        WorldMap.setPreserveRatio(true);
        VBox box = new VBox(20);
        box.setTranslateY(100);
        box.setTranslateX(1200);
        VBox testAreaBox = new VBox(20);
        testAreaBox.setTranslateY(400);
        testAreaBox.setTranslateX(1150);
        TextArea path = new TextArea();
        TextArea shortestPath = new TextArea();
        path.setMaxSize(205, 100);
        shortestPath.setMaxSize(205, 70);
        Label sourceLable = new Label("Source");
        sourceLable.setFont(new Font(30));
        Label targetLable = new Label("Target");
        targetLable.setFont(new Font(30));
        ChoiceBox source = new ChoiceBox();
        source.setMaxSize(100, 100);
        ChoiceBox target = new ChoiceBox();
        target.setMaxSize(100, 100);
        Button run = new Button("Run");
        Button clear = new Button("Clear");
        clear.setPrefWidth(100);
        run.setPrefWidth(100);
        testAreaBox.getChildren().addAll(path, shortestPath);
        root.getChildren().addAll(WorldMap, box);
        File f = new File("C:\\Users\\hp\\Desktop\\Algorithem\\DijkstraProject\\src\\main\\resources\\City data.txt");
        Scanner sc = new Scanner(f);
        int index = 1;
        String firstline = sc.nextLine();
        String[] detailss = firstline.split(" ");
        int numberOfVertices = Integer.parseInt(detailss[0]);
        int numberOfAdjacent = Integer.parseInt(detailss[1]);
        String[] arrCountry = new String[numberOfVertices];
        Circle[] circles = new Circle[numberOfVertices];
        String countryName = "";
        double countryLatitude = 0;
        double countryLongitude = 0;
        Vertex vertex = null;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] details = line.split(" ");
            if (index == numberOfVertices + 1)
                break;
            else {
                circles[index - 1] = new Circle();
                countryName = details[0];
                arrCountry[index - 1] = details[0];
                countryLatitude = Double.valueOf(details[1]);
                countryLongitude = Double.valueOf(details[2]);
                double xPosition = 0;
                double yPosition = 0;
                if (countryLongitude < 0 && countryLatitude > 0) {
                    xPosition = (1150.0 / 2.0) + (countryLongitude * 3.1048);// Longtidue
                    yPosition = (765.0 / 2.0) - (countryLatitude * 2.3611);// Latitude
                } else {
                    xPosition = (1150.0 / 2.0) + (countryLongitude * 3.2100);// Longtidue
                    yPosition = (765.0 / 2.0) - (countryLatitude * 2.2311);// Latitude
                }
                circles[index - 1].setId(details[0]);
                circles[index - 1].setRadius(3);
                circles[index - 1].setTranslateX(xPosition);
                circles[index - 1].setTranslateY(yPosition);
                root.getChildren().add(circles[index - 1]);
                vertex = new Vertex(new Country(countryName, countryLatitude, countryLongitude, circles[index - 1]));
                graph.addNode(vertex);
                graph.getVertices().stream().forEach(v -> vertices.put(v.getCountry().getCountryName(), v));//global vertices
            }
            index++;
        }
        for (int i = 0; i < numberOfVertices; i++) {
            source.getItems().add(circles[i].getId());
            target.getItems().add(circles[i].getId());
        }
        chooesFromMap(circles, source, target,numberOfVertices);
        readAjc(numberOfAdjacent);
        run.setOnAction(e -> {
            ShortPath shortPath = new ShortPath();
            String sou = source.getSelectionModel().getSelectedItem().toString();
            String tar = target.getSelectionModel().getSelectedItem().toString();
            shortPath = findPath(sou, tar);
            shortestPath.appendText(" " + shortPath.getDistance() + " KM");
            Line [] lines = new Line[shortPath.getCountries().size()-1];
            for (int i = 0; i < shortPath.getCountries().size() - 1; i++) {
                path.appendText(shortPath.getCountries().get(i).getCountryName() + "\n" );
                path.appendText( "|\n" );
                if (i == shortPath.getCountries().size() - 2)
                    path.appendText(shortPath.getCountries().get(i + 1).getCountryName());
                Line line  = new Line();
                line.setStrokeWidth(2);
                line.setStartX(shortPath.getCountries().get(i).getMapLocation().getTranslateX());
                line.setStartY(shortPath.getCountries().get(i).getMapLocation().getTranslateY());
                line.setEndX(shortPath.getCountries().get(i + 1).getMapLocation().getTranslateX());
                line.setEndY(shortPath.getCountries().get(i + 1).getMapLocation().getTranslateY());
                linePath.add(line);
                root.getChildren().add(line);
            }
        });
        clear.setOnAction(e -> {
            path.clear();
            shortestPath.clear();
            for (int i =0 ; i < linePath.size();i++){
                linePath.get(i).setEndX(linePath.get(i).getStartX());
                linePath.get(i).setEndY(linePath.get(i).getStartY());
            }
        });
        root.getChildren().add(testAreaBox);
        box.getChildren().addAll(sourceLable, source, targetLable, target, run, clear);
        Scene scene = new Scene(root, 1250, 670);
        stage.setTitle("Dijkstra");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

    public static ShortPath findPath(String sourceCountryName, String destinationCountryName) {
        Vertex source = vertices.get(sourceCountryName);
        Vertex destination = vertices.get(destinationCountryName);
        calculateShortestPathFromSource(source);
        List<Country> path = new LinkedList<>();
        destination.getShortestPath().forEach(vertex -> path.add(vertex.getCountry())); // add country  in  shortest path to path list
        if (path.size() != 0 || sourceCountryName.equals(destinationCountryName)) {
            path.add(destination.getCountry());
        }
        ShortPath shortPath = new ShortPath(path, destination.getDistance() *111);
        return shortPath;
    }

    public static void calculateShortestPathFromSource(Vertex source) {
        graph.getVertices().forEach(vertex -> {
            vertex.setDistance(Double.MAX_VALUE);
            vertex.getShortestPath().clear();
            vertex.setKnown(false);
        });
        source.setDistance(0d);
        PriorityQueue<Vertex> verticesHeap = new PriorityQueue<>();

        verticesHeap.add(source);

        while (verticesHeap.size() != 0) {
            Vertex currentVertex = verticesHeap.poll();
            //iterate for each adjacent vertex of the current vertex
            currentVertex.getAdjacentVertices().forEach((vertex, distance) -> {
                if (!vertex.isKnown()) {
                    // set new value for the vertex if the distance from the current vertex
                    // less then the already calculated distance
                    // and set the shortest path
                    if (currentVertex.getDistance() + distance < vertex.getDistance()) {
                        vertex.setDistance(currentVertex.getDistance() + distance);
                        // get the shortest path from the source to the current
                        // add the the current
                        // set it as the shortest path to the vertex
                        List<Vertex> shortestPath = new LinkedList<>(currentVertex.getShortestPath());
                        shortestPath.add(currentVertex);
                        vertex.setShortestPath(shortestPath);
                    }
                    verticesHeap.add(vertex);
                }
            });
            currentVertex.setKnown(true);
        }
    }

    public static void randomAjc(String[] arrCountry, int numberOfAdjacent) {
        int counter = 0;
        Vertex firstV = null;
        Vertex secondV = null;
        while (counter < numberOfAdjacent) {
            Random random = new Random();
            int x = random.nextInt(arrCountry.length);
            int y = random.nextInt(arrCountry.length);
            if (x != y) {
                firstV = vertices.get(arrCountry[x]);
                secondV = vertices.get(arrCountry[y]);
                firstV.addAdjacent(secondV);
                System.out.println(arrCountry[x] + " " + arrCountry[y]);
                counter++;
            }
        }
    }
    public static void readAjc(int numberOfAdjacent) throws FileNotFoundException {
        File f = new File("C:\\Users\\hp\\Desktop\\Algorithem\\DijkstraProject\\src\\main\\resources\\Adjacent.txt");
        Scanner sc = new Scanner(f);
        Vertex firstV = null;
        Vertex secondV = null;
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String[] details = line.split(" ");
            firstV = vertices.get(details[0]);
            secondV = vertices.get(details[1]);
            firstV.addAdjacent(secondV);
        }
    }
    public static void chooesFromMap(Circle[] circles, ChoiceBox source, ChoiceBox target,int numberOfVertices ) {
        final int[] sourceOrTarget = {1};
        for (int i = 0; i < numberOfVertices; i++) {
            int finalI = i;
            circles[i].setOnMouseExited(e -> {
                circles[finalI].setRadius(3.0);
            });
            int finalI1 = i;
            circles[i].setOnMouseMoved(e -> {
                circles[finalI1].setRadius(10);
                Tooltip t = new Tooltip(circles[finalI1].getId());
                Tooltip.install(circles[finalI1], t);
            });
            int finalI4 = i;
            circles[i].setOnMouseClicked(e -> {
                if (sourceOrTarget[0] == 1) {
                    source.setValue(circles[finalI4].getId());
                    sourceOrTarget[0]++;
                } else {
                    target.setValue(circles[finalI4].getId());
                    sourceOrTarget[0]--;
                }
            });
        }
    }
}