package controllers;

import models.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FirstSceneController {
    //ArrayList for storing all input node(Depot & Customer)
    public static ArrayList<Customer> node = new ArrayList<>();
    //Number of node input
    public static int noOfNode = 0;
    //Max capacity of vehicle
    public static int maxCapacity = 0;

    @FXML
    private TextField textFileName;
    @FXML
    private Label errorLabel;

    @FXML
    public void startButton(ActionEvent actionEvent) throws IOException {
        node.clear();   //Clear all elements in ArrayList
        Customer.setNumForID(-1);   //Reset ID

        boolean inputError = false;

        //Input column empty
        if (textFileName.getText().isEmpty()) {
            errorLabel.setText("Please input the file name!");
        } else {
            //File I/O
            try {
                Scanner input = new Scanner(new FileInputStream(textFileName.getText()));

                noOfNode = input.nextInt();
                maxCapacity = input.nextInt();

                for (int i = 0; i < noOfNode; i++) {
                    //Constructor of class controllers.Customer
                    //Parameter(x-coordinate, y-coordinate, Demands)
                    node.add(new Customer(input.nextInt(), input.nextInt(), input.nextInt()));
                }

                input.close();
                //Input file not found
            } catch (FileNotFoundException e) {
                inputError = true;
                System.out.println("File was not found");
                errorLabel.setText("The file is not found");
            }

            //If not error
            if (!inputError) {
                System.out.println("NoOfNode: " + noOfNode);
                System.out.println("MaxCapacity: " + maxCapacity);
                System.out.println("All node: " + node.toString().substring(1, node.toString().length() - 1) + "\n");

                Parent secondScene = FXMLLoader.load(getClass().getResource("/views/secondSceneView.fxml"));
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow(); // Obtain the current stage
                Scene scene = new Scene(secondScene);
                stage.setScene(scene); // Set the scene of the obtained stage into the new scene
                stage.show();
            }
        }
    }
}
