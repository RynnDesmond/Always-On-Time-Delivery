package models;

public class Customer {
    //Variable used to trace number of ID
    private static int numForID = -1;
    //ID of Depot and Customer
    private int id;
    //x-coordinate
    private int x;
    //y-coordinate
    private int y;
    //Goods
    private int demands;
    //Variable distance, aka cost
    private double distance = 0;
    //Depot or Customer
    private String name;

    /**
     * Constructor for class Customer
     *
     * @param x       :x-coordinate
     * @param y       :y-coordinate
     * @param demands :Goods possessed
     */
    public Customer(int x, int y, int demands) {
        this.x = x;
        this.y = y;
        this.demands = demands;
        //Trace number of ID
        numForID++;
        id = numForID;
        if (id == 0) {
            name = "Depot ID=" + id;
        } else {
            name = "Customer ID=" + id;
        }
    }

    //Getter & Setter
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDemands() {
        return demands;
    }

    public void setDemands(int demands) {
        this.demands = demands;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(Customer o) {
        //Formula to calculate Euclidean distance
        this.distance = Math.sqrt(Math.pow((x - o.getX()), (2)) + Math.pow((y - o.getY()), (2)));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getNumForID() {
        return numForID;
    }

    public static void setNumForID(int numForID) {
        Customer.numForID = numForID;
    }

    //Display format
    @Override
    public String toString() {
        return "Customer{" + "x=" + x + ", y=" + y + ", demands=" + demands + '}';
    }
}
