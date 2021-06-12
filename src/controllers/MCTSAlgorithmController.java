package controllers;

import models.Customer;
import models.Tour;
import models.Vehicle;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MCTSAlgorithmController implements Initializable {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label infoLabel;

    @FXML
    private Label timerLabel;

    private Tour a_tour;

    private final int level = 3;
    private final int iterations = 100;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setStyle("-fx-accent: #00FF00;");

        //Create task
        MCTS taskMCTS = new MCTS(FirstSceneController.node, FirstSceneController.maxCapacity) {
            //Method called in task
            @Override
            protected Tour call() {
                return search(level, iterations);
            }
        };
        //When task succeeded, get the return value(a_tour)
        taskMCTS.setOnSucceeded(event -> a_tour = taskMCTS.getValue());
        //property of progress bar updates with task progress
        progressBar.progressProperty().bind(taskMCTS.progressProperty());
        //property of timer label updates with task progress
        timerLabel.textProperty().bind(taskMCTS.messageProperty());
        //Create new thread for the task to run concurrently (So GUI will not be frozen)
        new Thread(taskMCTS).start();
    }

    //RESULT button
    public void nextSceneOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/mctsSimulationView.fxml"));
        Parent nextScene = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(nextScene);
        MCTSGraphController controller = loader.getController();
        //Pass value
        controller.initData(a_tour);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //Inner class (MCTS Algorithm)
    class MCTS extends Task<Tour> {
        //Arraylist to store the original input node(All including depot)
        private final ArrayList<Customer> node;
        //Max capacity of vehicle
        private final int maxCapacity;
        private final Customer depot;

        //3D array
        private final double[][][] policy;
        //globalPolicy is one of the policy level used by rollout method
        private double[][] globalPolicy;
        //Auto initialize to zero ad

        //A variable to count the second
        private int timer = 0;
        //A variable to store the starting time in nanosecond
        private final long startTime = System.nanoTime();

        /**
         * Constructor for class MCTS
         *
         * @param node        :All input node
         * @param maxCapacity :Max capacity of vehicle
         */
        @SuppressWarnings("unchecked")
        public MCTS(ArrayList<Customer> node, int maxCapacity) {
            //Used clone method to avoid getting Arraylist reference
            this.node = (ArrayList<Customer>) node.clone();
            this.maxCapacity = maxCapacity;
            this.depot = node.get(0);
            //n = number of customers + depot
            int n = node.size();
            int level = 3;
            policy = new double[level][n][n];
            globalPolicy = new double[n][n];
        }

        //Initialize best_tour with positive infinity cost
        Tour best_tour = new Tour(Double.POSITIVE_INFINITY);

        /**
         * Main function to perform MCTS with NPRA search
         *
         * @param level      :Number of level for MCTS search tree
         * @param iterations :Number of nodes in every level
         * @return a_tour :Object of class Tour, convey information about tour cost, all routes and etc
         */
        public Tour search(int level, int iterations) {
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

            if (level == 0) {
                return rollout();
            } else {
                policy[level - 1] = globalPolicy;
                for (int i = 0; i < iterations; i++) {
                    Tour new_tour = search(level - 1, iterations);
                    if (new_tour.getTourCost() < best_tour.getTourCost()) {
                        best_tour = new_tour;
                        adapt(best_tour, level);
                    }
                    // If the searching time is far too long then directly return the best tour we can search of after limited time
                    if ((System.nanoTime() - startTime) / 1000000000 > 60) {
                        return best_tour;
                    }
                }
                globalPolicy = policy[level - 1];
            }
            return best_tour;
        }

        /**
         * Function to update policy based on rollout result (a tour)
         *
         * @param a_tour: Object of class Tour
         * @param level:  Number of level for MCTS search tree
         */
        public void adapt(Tour a_tour, int level) {
            ArrayList<Customer> visited = new ArrayList<>();
            //For every route in a_tour
            for (int i = 0; i < a_tour.getRouteSize(); i++) {
                //For every stop in route
                for (int j = 0; j < a_tour.getStopSize(i) - 1; j++) {
                    //Hyperparameter to update the policy (Learning rate)
                    int ALPHA = 1;
                    //policy[level][stop_ID][next_stop_ID]
                    policy[level - 1][a_tour.getRoute(i).getID(j)][a_tour.getRoute(i).getID(j + 1)] += ALPHA;
                    double z = 0.0;
                    //For every possible move that can be made by stop
                    for (int k = 0; k < node.size(); k++) {
                        if (node.get(k).getId() != a_tour.getRoute(i).getID(j)) {       //Excluding itself
                            //If the move is not visited yet
                            if (!visited.contains(node.get(k))) {
                                //globalPolicy[stop_ID][move_ID])
                                z += Math.exp(globalPolicy[a_tour.getRoute(i).getID(j)][node.get(k).getId()]);
                            }
                        }
                    }
                    //For every possible move that can be made by stop
                    for (int k = 0; k < node.size(); k++) {
                        if (node.get(k).getId() != a_tour.getRoute(i).getID(j)) {       //Excluding itself
                            //If the move is not visited yet
                            if (!visited.contains(node.get(k))) {
                                policy[level - 1][a_tour.getRoute(i).getID(j)][node.get(k).getId()] -= ALPHA * (Math.exp(globalPolicy[a_tour.getRoute(i).getID(j)][node.get(k).getId()]) / z);
                            }
                        }
                    }
                    //Set stop as visited
                    visited.add(a_tour.getRoute(i).getVisited(j));
                }
            }
        }

        /**
         * Function that search for routes for the tour according to policy
         *
         * @return a_tour: Object of class Tour
         */
        @SuppressWarnings("unchecked")
        public Tour rollout() {
            Customer currentStop;
            Customer nextStop;
            //Possible successors excluding depot
            ArrayList<Customer> possible_successors = (ArrayList<Customer>) node.clone();
            //So remove depot
            possible_successors.remove(0);

            ArrayList<Customer> visited = new ArrayList<>();
            ArrayList<Customer> checked = new ArrayList<>();
            //Initialize new_tour with first route with first stop at 0(Every route must start and end at depot(ID=0))
            Tour new_tour = new Tour();
            new_tour.addRoute(new Vehicle(depot, maxCapacity));

            while (true) {
                //currentStop = new_tour last route last stop
                currentStop = new_tour.getRoute(new_tour.getRouteSize() - 1).getVisited(new_tour.getStopSize(new_tour.getRouteSize() - 1) - 1);     //Check if got error
                //Find every possible successors that is not yet checked for the currentStop
                for (int i = 0; i < possible_successors.size(); i++) {
                    if (checked.contains(possible_successors.get(i)) || visited.contains(possible_successors.get(i))) {
                        possible_successors.remove(i);
                    }
                }
                //If no successors is available
                if (possible_successors.isEmpty()) {
                    //CurrentRoute is completed and should return to depot
                    new_tour.getRoute(new_tour.getRouteSize() - 1).addVisited(depot);
                    new_tour.getRoute(new_tour.getRouteSize() - 1).setMCTSRouteCost(calculateCost(currentStop, depot));

                    //If all stop are visited
                    if (checked.isEmpty()) {
                        for (int i = 0; i < new_tour.getRouteSize(); i++) {
                            //Sum up all the route costs
                            new_tour.setTourCost(new_tour.getTourCost() + new_tour.getRoute(i).getRouteCost());
                        }
                        break;  //Rollout process is done
                    }
                    //Add new route(Vehicle) into new_tour(Again start at depot with ID=0)
                    new_tour.addRoute(new Vehicle(depot, maxCapacity));

                    for (int i = 0; i < checked.size(); i++) {
                        possible_successors.add(checked.remove(i));
                    }
                    continue;  //Skip to next loop to continue search a route for new vehicle
                }

                nextStop = select_next_move(currentStop, possible_successors);
                //If adding nextStop to currentRoute does not violate any rules(Max capacity)
                if (!new_tour.getRoute(new_tour.getRouteSize() - 1).exceed(nextStop)) {
                    //Add nextStop to currentRoute
                    new_tour.getRoute(new_tour.getRouteSize() - 1).addVisited(nextStop);
                    new_tour.getRoute(new_tour.getRouteSize() - 1).setMCTSRouteCost(calculateCost(currentStop, nextStop));
                    //Set nextStop as visited
                    visited.add(nextStop);
                } else {
                    //Set nextStop as checked
                    checked.add(nextStop);
                }
            }
            return new_tour;
        }

        /**
         * Function that decide which node to go next from current node based on the policy
         *
         * @param currentStop         :From what Stop
         * @param possible_successors :To what Stop(A numbers of)
         * @return a_stop :A stop selected from possible_successors
         */
        public Customer select_next_move(Customer currentStop, ArrayList<Customer> possible_successors) {
            double[] probability = new double[possible_successors.size()];
            double sum = 0;

            for (int i = 0; i < possible_successors.size(); i++) {
                probability[i] = Math.exp(globalPolicy[currentStop.getId()][possible_successors.get(i).getId()]);
                sum += probability[i];
            }

            double mrand = new Random().nextDouble() * sum;
            int i = 0;
            sum = probability[0];
            while (sum < mrand) {
                sum += probability[++i];
            }
            return possible_successors.get(i);
        }

        /**
         * Calculate Euclidean distance
         *
         * @param stop1 :Object of class Customer
         * @param stop2 :Object of class Customer
         * @return Euclidean distance(Cost) between two Stop
         */
        public double calculateCost(Customer stop1, Customer stop2) {
            return Math.sqrt(Math.pow((stop1.getX() - stop2.getX()), (2)) + Math.pow((stop1.getY() - stop2.getY()), (2)));
        }

        /**
         * Task call
         *
         * @return a_tour :Object of class Tour
         */
        @Override
        protected Tour call() {
            return search(level, iterations);
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