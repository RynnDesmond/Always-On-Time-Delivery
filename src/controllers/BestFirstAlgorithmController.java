package controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import models.Customer;
import models.Tour;
import models.Vehicle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class BestFirstAlgorithmController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label infoLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Button nextButton;

    private Tour a_tour;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Visibility of button(Not shown)
        nextButton.setVisible(false);
        //Color of the progress bar
        progressBar.setStyle("-fx-accent: #00FF00;");
        //Create task
        BestFirst taskBestFirst = new BestFirst(FirstSceneController.node, FirstSceneController.maxCapacity);

        //When task succeeded, get the return value(a_tour)
        taskBestFirst.setOnSucceeded(event -> a_tour = taskBestFirst.getValue());
        //property of progress bar updates with task progress
        progressBar.progressProperty().bind(taskBestFirst.progressProperty());
        //property of timer label updates with task progress
        timerLabel.textProperty().bind(taskBestFirst.messageProperty());
        //Create new thread for the task to run concurrently (So GUI will not be frozen)
        new Thread(taskBestFirst).start();
    }

    //RESULT button
    public void nextSceneOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/bestFirstSearchView.fxml"));
        Parent nextScene = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(nextScene);
        BestFirstGraphController controller = loader.getController();
        //Pass value
        controller.initData(a_tour);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //Inner class (Best First Algorithm)
    class BestFirst extends Task<Tour> {
        //Arraylist to store the node(Customer) have not been visited(Excluding depot)
        private ArrayList<Customer> nodeLeft;
        //Max capacity of vehicle
        private final int maxCapacity;
        private final Customer depot;
        private final Tour tour = new Tour();
        //A variable to count the second
        private int timer = 0;
        //A variable to store the starting time in nanosecond
        private final long startTime = System.nanoTime();

        /**
         * Constructor for class BestFirst
         *
         * @param node        :All input node
         * @param maxCapacity :Max capacity of vehicle
         */
        @SuppressWarnings("unchecked")
        public BestFirst(ArrayList<Customer> node, int maxCapacity) {
            //Remove depot from Arraylist nodeLeft
            this.nodeLeft = (ArrayList<Customer>) node.clone();
            this.depot = nodeLeft.remove(0);
            this.maxCapacity = maxCapacity;
        }

        //Best First search
        public Tour search() {
            //Method setAllCost :Calculate distance(Cost) between current node and each of all other nodes left
            nodeLeft = setAllCost(depot, nodeLeft);
            //Sort the nodes in ascending order by distance(Cost) calculated
            nodeLeft.sort(new SortByCost());
            //Loop until all nodes are visited
            while (!nodeLeft.isEmpty()) {
                //Time elapsed
                if ((System.nanoTime() - startTime) / 1000000000 == timer) {
                    System.out.print("\r");
                    System.out.print("Time elapsed: |");
                    for (int j = 0; j < timer; j++) {
                        System.out.print("=");
                    }
                    System.out.print("| " + timer + "s");
                    updateMessage(timer + " s");
                    updateProgress(++timer, 60);
                }
                // If the searching time is far too long then directly return the best tour we can search of after limited time
                if ((System.nanoTime() - startTime) / 1000000000 > 60) {
                    return tour;
                }

                //Create a route(Vehicle) and start at depot with ID=0
                Vehicle v = new Vehicle(depot, maxCapacity);

                for (int i = 0; i < nodeLeft.size(); i++) {
                    //If adding next stop to current route(Vehicle) violates rule(Max capacity)
                    if (v.exceed(nodeLeft.get(i))) {
                        continue;   //Skip adding process
                    }
                    //Add next stop to route(Vehicle) and remove it from nodeLeft
                    v.addVisited(nodeLeft.remove(i));
                }
                //Back to depot
                v.addVisited(depot);
                //Add route to tour
                tour.addRoute(v);
            }//Going to create next route

            tour.calculateTourCost();
            return tour;
        }

        /**
         * Calculate distance(Cost) between current node and each of all other nodes left
         *
         * @param current  :Object of class Customer
         * @param nodeLeft :List of objects of class Customer
         * @return List of objects of class Customer with distance(Cost) calculated
         */
        public ArrayList<Customer> setAllCost(Customer current, ArrayList<Customer> nodeLeft) {
            for (int i = 0; i < nodeLeft.size(); i++) {
                nodeLeft.get(i).setDistance(current);
            }
            return nodeLeft;
        }

        /**
         * Comparator to sort stop(Customer) in ascending order by distance
         */
        class SortByCost implements Comparator<Customer> {
            @Override
            public int compare(Customer o1, Customer o2) {
                if (o1.getDistance() < o2.getDistance()) {
                    return -1;
                } else if (o1.getDistance() > o2.getDistance()) {
                    return 1;
                }
                return 0;
            }
        }

        /**
         * Override task call
         *
         * @return a_tour :Object of class Tour
         */
        @Override
        protected Tour call() {
            return search();
        }

        /**
         * What will be executed when task succeeded
         */
        @Override
        protected void succeeded() {
            if (timer < 60) {
                System.out.println(" (Work done!)");
                infoLabel.setText("Work done!");
                nextButton.setVisible(true);    //Visibility of button(Shown)
            } else {
                System.out.println(" (Searching is forced to stop!)");
                infoLabel.setText("Searching is forced to stop!");
                nextButton.setVisible(true);    //Visibility of button(Shown)
            }
        }
    }
}