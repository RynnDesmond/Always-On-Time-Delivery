package controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class GreedyAlgorithmController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label infoLabel;

    @FXML
    private Label timerLabel;

    private Tour a_tour;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setStyle("-fx-accent: #00FF00;");

        //Create task
        Greedy taskGreedy = new Greedy(FirstSceneController.node, FirstSceneController.maxCapacity) {
            //Method called in task
            @Override
            protected Tour call() {
                return search();
            }
        };
        //When task succeeded, get the return value(a_tour)
        taskGreedy.setOnSucceeded(event -> a_tour = taskGreedy.getValue());
        //property of progress bar updates with task progress
        progressBar.progressProperty().bind(taskGreedy.progressProperty());
        //property of timer label updates with task progress
        timerLabel.textProperty().bind(taskGreedy.messageProperty());
        //Create new thread for the task to run concurrently (So GUI will not be frozen)
        new Thread(taskGreedy).start();
    }

    //RESULT button
    public void nextSceneOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/greedySimulationView.fxml"));
        Parent nextScene = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(nextScene);
        GreedyGraphController controller = loader.getController();
        //Pass value
        controller.initData(a_tour);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //Inner class (Greedy Algorithm)
    class Greedy extends Task<Tour> {
        //Arraylist to store the original input node(All including depot)
        private final ArrayList<Customer> node;
        //Arraylist to store the node(Customer) have not been visited(Excluding depot)
        private final ArrayList<Customer> nodeLeft;
        //Max capacity of vehicle
        private final int maxCapacity;
        private final Customer depot;
        private final Tour tour = new Tour();
        //A variable to count the second
        private int timer = 0;
        //A variable to store the starting time in nanosecond
        private final long startTime = System.nanoTime();

        /**
         * Constructor for class Greedy
         *
         * @param node        :All input node
         * @param maxCapacity :Max capacity of vehicle
         */
        @SuppressWarnings("unchecked")
        public Greedy(ArrayList<Customer> node, int maxCapacity) {
            //Used clone method to avoid getting Arraylist reference
            this.node = (ArrayList<Customer>) node.clone();
            //Remove depot from Arraylist nodeLeft
            this.nodeLeft = (ArrayList<Customer>) node.clone();
            this.depot = nodeLeft.remove(0);
            this.maxCapacity = maxCapacity;
        }

        /**
         * Method to start Greedy Search
         *
         * @return a_tour :Object of class Tour
         */
        public Tour search() {
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

                //Store the ID of current node(Customer) /Always start with ID=0(Depot)
                int currentID = 0;
                //Create a route(Vehicle) and start at depot with ID=0
                Vehicle v = new Vehicle(depot, maxCapacity);
                //All the possible successors excluding depot and visited customer
                ArrayList<Customer> possible_successors;

                while (true) {
                    if (nodeLeft.isEmpty()) {
                        break;  //No more customers
                    }
                    //Method setAllCost :Calculate distance(Cost) between current node and each of all other nodes left
                    possible_successors = setAllCost(node.get(currentID), nodeLeft);
                    //Sort the nodes in ascending order by distance(Cost) calculated
                    possible_successors.sort(new SortByCost());

                    boolean added = false;
                    for (int i = 0; i < possible_successors.size(); i++) {
                        //If adding next stop to current route(Vehicle) violates rule(Max capacity)
                        if (v.exceed(possible_successors.get(i))) {
                            continue;   //Skip adding process
                        }
                        //Add next stop to route(Vehicle)
                        v.addVisited(possible_successors.get(i));
                        //Remove the visited stop(Customer) from possible_successors
                        nodeLeft.remove(possible_successors.get(i));
                        //currentID moves to next
                        currentID = possible_successors.get(i).getId();
                        //A customer is added
                        added = true;
                        //Break for loop to renew the possible_successors
                        break;
                    }
                    if (!added) {
                        //Break while loop as no stop(Customer) can be added to the particular route already
                        break;
                    }
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
        @SuppressWarnings("unchecked")
        public ArrayList<Customer> setAllCost(Customer current, ArrayList<Customer> nodeLeft) {
            for (int i = 0; i < nodeLeft.size(); i++) {
                nodeLeft.get(i).setDistance(current);
            }
            return (ArrayList<Customer>) nodeLeft.clone();
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
         * Task call
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
            } else {
                System.out.println(" (Searching is forced to stop!)");
                infoLabel.setText("Searching is forced to stop!");
            }
        }
    }
}