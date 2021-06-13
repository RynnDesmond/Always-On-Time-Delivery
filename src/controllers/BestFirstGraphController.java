package controllers;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import models.Tour;
import models.Vehicle;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BestFirstGraphController implements Initializable {
    @FXML
    private Label tourCost;

    @FXML
    private Pane pane;

    @FXML
    private TableView<Vehicle> table;

    @FXML
    private TableColumn<Vehicle, String> vehicleCl;

    @FXML
    private TableColumn<Vehicle, String> routeCl;

    @FXML
    private TableColumn<Vehicle, String> capacityCl;

    @FXML
    private TableColumn<Vehicle, String> costCl;
    //Group to store content to display on Zoomable pane
    private final Group content_group = new Group();

    private Tour a_tour;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Create zoomable pane for displaying graph
        ZoomableScrollPane zoomPane = new ZoomableScrollPane(content_group);

        //Make the zoomable pane draggable
        ZoomableScrollPane.SceneGestures sceneGestures = new ZoomableScrollPane.SceneGestures(zoomPane);
        zoomPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        zoomPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());

        //Properties of zoomable pane
        zoomPane.setMinSize(650, 350);
        zoomPane.setLayoutX(74);
        zoomPane.setLayoutY(46);
        zoomPane.setStyle("-fx-border-color: black");
        pane.getChildren().add(zoomPane);

        //Table
        vehicleCl.setCellValueFactory(new PropertyValueFactory<>("id"));
        routeCl.setCellValueFactory(new PropertyValueFactory<>("route"));
        capacityCl.setCellValueFactory(new PropertyValueFactory<>("occupied"));
        costCl.setCellValueFactory(new PropertyValueFactory<>("routeCost"));

        //Lines to run after initialization
        Platform.runLater(() -> {
            setLine(a_tour);
            setNode();
            tourCost.setText(String.valueOf(a_tour.getTourCost()));
            System.out.println("Best First Search");
            System.out.println(a_tour);
            for (int a = 0; a < a_tour.getRouteSize(); a++) {
                a_tour.getRoute(a).setId(a + 1);
                table.getItems().add(a_tour.getRoute(a));
            }
        });
    }

    //Initialize the tour data (Pass from previous scene)
    public void initData(Tour tour) {
        a_tour = tour;
    }

    /**
     * Point all nodes on graph(Zoomable pane)
     */
    public void setNode() {
        //Create circle for depot
        Circle nodeDepot = new Circle(2);
        nodeDepot.setFill(Color.LIGHTSALMON);
        nodeDepot.setLayoutX((FirstSceneController.node.get(0).getX()) + 300);
        nodeDepot.setLayoutY((-FirstSceneController.node.get(0).getY() + 100) + 300);
        //Create text for depot
        Text textDepot = new Text();
        textDepot.setFont(new Font(2));
        textDepot.setText("   D0\n(" + FirstSceneController.node.get(0).getX() + "," + FirstSceneController.node.get(0).getY() + ")");
        textDepot.setLayoutX(FirstSceneController.node.get(0).getX() + 297);
        textDepot.setLayoutY((-FirstSceneController.node.get(0).getY() + 100) + 300);
        //Add depot Node & Text into content_group
        content_group.getChildren().addAll(nodeDepot, textDepot);

        //Add all customer Nodes & Texts into content_group
        for (int i = 1; i < FirstSceneController.node.size(); i++) {
            Circle nodeCustomer = new Circle(2);
            nodeCustomer.setFill(Color.LIGHTGREEN);
            nodeCustomer.setLayoutX(FirstSceneController.node.get(i).getX() + 300);
            nodeCustomer.setLayoutY((-FirstSceneController.node.get(i).getY() + 100) + 300);

            Text textCustomer = new Text();
            textCustomer.setFont(new Font(2));
            textCustomer.setText("   C" + FirstSceneController.node.get(i).getId() + "\n(" + FirstSceneController.node.get(i).getX() + "," + FirstSceneController.node.get(i).getY() + ")");
            textCustomer.setLayoutX(FirstSceneController.node.get(i).getX() + 297);
            textCustomer.setLayoutY((-FirstSceneController.node.get(i).getY() + 100) + 300);

            content_group.getChildren().addAll(nodeCustomer, textCustomer);
        }
    }

    /**
     * Transition of the circles
     *
     * @param tour :Object of class Tour
     */
    public void setPath(Tour tour) {
        ArrayList<Circle> circleList = new ArrayList<>();

        for (int i = 0; i < tour.getRouteSize(); i++) {
            Circle circles = new Circle(tour.getRoute(i).getVisited(0).getX() + 300, (-tour.getRoute(i).getVisited(0).getY() + 100) + 300, 1);
            circles.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            circleList.add(circles);
        }

        for (int i = 0; i < circleList.size(); i++) {
            ArrayList<TranslateTransition> transitionList = new ArrayList<>();

            for (int j = 0; j < tour.getRoute(i).getVisitedSize() - 1; j++) {
                //Create a translate transition
                TranslateTransition transition = new TranslateTransition(Duration.millis(10000), circleList.get(i));
                transition.setByX((tour.getRoute(i).getVisited(j + 1).getX() + 300) - (tour.getRoute(i).getVisited(j).getX() + 300));
                transition.setByY(((-tour.getRoute(i).getVisited(j + 1).getY() + 100) + 300) - ((-tour.getRoute(i).getVisited(j).getY() + 100) + 300));
                transitionList.add(transition);
            }
            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().addAll(transitionList);
            sequentialTransition.play();
            content_group.getChildren().addAll(circleList.get(i));
        }
    }

    /**
     * Set line between nodes on graph(Zoomable pane)
     *
     * @param tour :Object of class Tour
     */
    public void setLine(Tour tour) {
        ArrayList<Line> lineList = new ArrayList<>();

        for (int i = 0; i < tour.getRouteSize(); i++) {
            Color randomColor = Color.color(Math.random(), Math.random(), Math.random());

            for (int j = 0; j < tour.getRoute(i).getVisitedSize() - 1; j++) {
                Line line = new Line(tour.getRoute(i).getVisited(j).getX() + 300, (-tour.getRoute(i).getVisited(j).getY() + 100) + 300, tour.getRoute(i).getVisited(j + 1).getX() + 300, (-tour.getRoute(i).getVisited(j + 1).getY() + 100) + 300);
                line.setStroke(randomColor);
                lineList.add(line);
            }
        }
        content_group.getChildren().addAll(lineList);
    }

    //BACK button
    public void backOnAction(ActionEvent event) throws IOException {
        Parent secondScene = FXMLLoader.load(getClass().getResource("/views/secondSceneView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(secondScene);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //PLAY button
    public void playOnAction() {
        setPath(a_tour);
    }
}
