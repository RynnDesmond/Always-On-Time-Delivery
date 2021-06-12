package models;

import java.util.ArrayList;

public class Tour {
    //ArrayList to store all the route(Vehicle)
    private ArrayList<Vehicle> route = new ArrayList<>();
    private double tourCost;

    /**
     * Empty constructor for class Tour
     */
    public Tour() {

    }

    /**
     * Another constructor for class Tour
     *
     * @param tourCost :Initialize the tourCost
     */
    public Tour(double tourCost) {
        this.tourCost = tourCost;
    }

    //Getter & Setter
    public double getTourCost() {
        return tourCost;
    }

    public void setTourCost(double tourCost) {
        this.tourCost = tourCost;
    }

    /**
     * Calculate and Sum up all the route costs to get tour cost
     * For Greedy and Best First
     */
    public void calculateTourCost() {
        double total = 0;

        for (int i = 0; i < route.size(); i++) {
            route.get(i).calculateRouteCost();
            total += route.get(i).getRouteCost();
        }
        setTourCost(total);
    }

    /**
     * Sum up all the route costs to get tour cost
     * For Basic Search
     */
    public void calculateBasicTourCost() {
        double total = 0;
        for (int i = 0; i < route.size(); i++) {
            total += route.get(i).getRouteCost();
        }
        setTourCost(total);
    }

    public int getRouteSize() {
        return route.size();
    }

    public int getStopSize(int index) {
        return route.get(index).getVisitedSize();
    }

    public Vehicle getRoute(int index) {
        return route.get(index);
    }

    /**
     * Add a new route into Arraylist route
     *
     * @param a_route :Route, aka Vehicle
     */
    public void addRoute(Vehicle a_route) {
        route.add(a_route);
    }

    //Display format
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Tour").append("\nTour Cost: ").append(tourCost);

        for (int i = 0; i < route.size(); i++) {
            sb.append("\nVehicle ").append(i + 1).append("\n");

            for (int j = 0; j < route.get(i).getVisitedSize(); j++) {

                if (j == route.get(i).getVisitedSize() - 1) {
                    sb.append(route.get(i).getVisited(j).getId());
                } else {
                    sb.append(route.get(i).getVisited(j).getId()).append(" -> ");
                }
            }
            sb.append("\nCapacity: ").append(route.get(i).getOccupied()).append("\nCost: ").append(route.get(i).getRouteCost());
        }
        sb.append("\n");
        return sb.toString();
    }
}
