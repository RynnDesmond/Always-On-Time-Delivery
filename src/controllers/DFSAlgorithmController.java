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
import java.util.Collections;
import java.util.ResourceBundle;

public class DFSAlgorithmController implements Initializable {

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
        Basic taskBasic = new Basic(FirstSceneController.node, FirstSceneController.maxCapacity);

        //When task succeeded, get the return value(a_tour)
        taskBasic.setOnSucceeded(event -> a_tour = taskBasic.getValue());
        //property of progress bar updates with task progress
        progressBar.progressProperty().bind(taskBasic.progressProperty());
        //property of timer label updates with task progress
        timerLabel.textProperty().bind(taskBasic.messageProperty());
        //Create new thread for the task to run concurrently (So GUI will not be frozen)
        new Thread(taskBasic).start();
    }

    //RESULT button
    public void nextSceneOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/basicSimulationView.fxml"));
        Parent nextScene = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(nextScene);
        BasicGraphController controller = loader.getController();
        //Pass value
        controller.initData(a_tour);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //Inner class (Basic Algorithm)
    class Basic extends Task<Tour> {
        //Arraylist to store the original input node(All including depot)
        private final ArrayList<Customer> node;
        //Arraylist to store the node(Customer excluding depot)
        private final ArrayList<Customer> customer;
        //Max capacity of vehicle
        private final int maxCapacity;
        //Depot
        private final Customer depot;
        //ArrayList to store the ID of customer to be permuted
        private final ArrayList<Integer> permutation = new ArrayList<>();
        //Initialize tourWithLowestCost with positive infinity tour cost first
        private Tour tourWithLowestCost = new Tour(Double.POSITIVE_INFINITY);
        //A variable to count the second
        private int timer = 0;
        //A variable to store the starting time in nanosecond
        private final long startTime = System.nanoTime();

        /**
         * Constructor for class Basic
         *
         * @param node        :All input node
         * @param maxCapacity :Max capacity of vehicle
         */
        @SuppressWarnings("unchecked")
        public Basic(ArrayList<Customer> node, int maxCapacity) {
            //Used clone method to avoid getting Arraylist reference
            this.node = (ArrayList<Customer>) node.clone();
            //Remove depot from Arraylist customer
            this.customer = (ArrayList<Customer>) node.clone();
            this.depot = customer.remove(0);
            this.maxCapacity = maxCapacity;
        }

        /**
         * Method to start Depth First Search
         *
         * @return a_tour :Object of class Tour
         */
        public Tour DFS() {
            //Add Customer ID into ArrayList permutation
            for (int i = 1; i <= customer.size(); i++) {
                permutation.add(i);
            }
            //Start permutation
            return permute(permutation, 0);
        }

        /**
         * Method to permute all the customer using ID
         *
         * @param permutation :ArrayList to store the ID of customer to be permuted
         * @param index       :Where to start permute
         * @return a_tour :Object of class Tour
         */
        public Tour permute(ArrayList<Integer> permutation, int index) {

            for (int i = index; i < permutation.size(); i++) {
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
                //Permutation
                Collections.swap(permutation, i, index);
                permute(permutation, index + 1);
                Collections.swap(permutation, index, i);
                // If the searching time is far too long then directly return the best tour we can search of after limited time
                if ((System.nanoTime() - startTime) / 1000000000 > 60) {
                    return tourWithLowestCost;
                }
            }
            //A permutation done
            //Start to form a tour by using the permutation
            if (index == permutation.size() - 1) {
                //Create a new tour
                Tour tour = new Tour();
                //Copy the permutation to another ArrayList called nodeLeft
                ArrayList<Integer> nodeLeft = (ArrayList<Integer>) permutation.clone();

                //Loop until all node are visited
                while (!nodeLeft.isEmpty()) {
                    //Create a route(Vehicle) and start at depot with ID=0
                    Vehicle v = new Vehicle(depot, maxCapacity);
                    //Loop until vehicle encounters a customer with demands higher than its capacity
                    while (true) {
                        if (nodeLeft.isEmpty()) {
                            break;  //No more customers
                        }
                        //If adding next stop to current route(Vehicle) not violates rule(Max capacity)
                        if (!v.exceed(node.get(nodeLeft.get(0)))) {
                            //Add next stop to route(Vehicle)
                            //And remove it from nodeLeft
                            v.addVisited(node.get(nodeLeft.remove(0)));
                        } else {
                            //Vehicle encounters a customer with demands higher than its capacity
                            break;
                        }
                    }
                    //Back to depot
                    v.addVisited(depot);
                    //Calculate the route cost of this vehicle
                    v.calculateBasicRouteCost();
                    //Add route to tour
                    tour.addRoute(v);
                } //One tour done

                //Calculate the tour cost of this tour
                tour.calculateBasicTourCost();

                //Compare if the tour cost of new tour is lower than tourWithLowestCost
                if (tour.getTourCost() < tourWithLowestCost.getTourCost()) {
                    //If yes
                    tourWithLowestCost = tour;
                }
            }
            return tourWithLowestCost;
        }

        /**
         * Override task call
         *
         * @return a_tour :Object of class Tour
         */
        @Override
        protected Tour call() {
            return DFS();
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


