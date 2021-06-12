package models;

import java.util.ArrayList;

public class Vehicle {
    private int id;
    //Arraylist to store Customer(Stop) visited by particular vehicle
    private final ArrayList<Customer> visited = new ArrayList<>();
    //Capacity of particular vehicle being occupied
    private int occupied = 0;
    private final int maxCapacity;
    private double routeCost = 0;
    private String route = "0";

    /**
     * Constructor for class Vehicle
     *
     * @param depot       :Starting point
     * @param maxCapacity :Max capacity of Vehicle
     */
    public Vehicle(Customer depot, int maxCapacity) {
        //Set the starting point(Always at depot,id=0)
        visited.add(depot);
        this.maxCapacity = maxCapacity;
    }

    /**
     * Add Customer(Stop) visited
     *
     * @param stop :Object of class Customer
     */
    public void addVisited(Customer stop) {
        visited.add(stop);
        //Add the demands got at the Customer(Stop) visited to variable occupied
        occupied += stop.getDemands();
        route += " -> " + stop.getId();
    }

    /**
     * Check if the demands got at the Customer(Stop) visited exceed the max capacity of particular vehicle
     *
     * @param stop :Object of class Customer
     * @return True if exceed, otherwise false
     */
    public boolean exceed(Customer stop) {
        return (occupied + stop.getDemands()) > maxCapacity;
    }

    //Getter & Setter
    public int getID(int index) {
        return visited.get(index).getId();
    }

    public int getVisitedSize() {
        return visited.size();
    }

    public Customer getVisited(int index) {
        return visited.get(index);
    }

    public int getOccupied() {
        return occupied;
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
     * Sum up all the Euclidean distances between 2 Stops to get cost for 1 route
     * Used by Greedy search
     */
    public void calculateRouteCost() {
        for (int j = 1; j < visited.size() - 1; j++) {
            routeCost += visited.get(j).getDistance();
        }
        routeCost += calculateCost(visited.get(visited.size() - 2), visited.get(visited.size() - 1));
    }

    /**
     * Add a Euclidean distances between 2 Stops into variable routeCost
     * Used by MCTS search
     *
     * @param cost :Euclidean distances between 2 Stops
     */
    public void setMCTSRouteCost(double cost) {
        routeCost += cost;
    }

    public double getRouteCost() {
        return routeCost;
    }

    /**
     * Calculate route cost for Basic search
     */
    public void calculateBasicRouteCost() {
        for (int j = 0; j < visited.size() - 1; j++) {
            routeCost += calculateCost(visited.get(j), visited.get(j + 1));
        }
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
