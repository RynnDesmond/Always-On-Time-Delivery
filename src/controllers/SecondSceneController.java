package controllers;

import models.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SecondSceneController implements Initializable {
    @FXML
    private TableView table;

    @FXML
    private Label numOfNode;

    @FXML
    private Label maxCap;

    @FXML
    private TableColumn<Customer, String> nodeCl;

    @FXML
    private TableColumn<Customer, String> xCl;

    @FXML
    private TableColumn<Customer, String> yCl;

    @FXML
    private TableColumn<Customer, String> demandsCl;

    public void initialize(URL Location, ResourceBundle arg1) {
        //Table
        nodeCl.setCellValueFactory(new PropertyValueFactory<>("name"));
        xCl.setCellValueFactory(new PropertyValueFactory<>("x"));
        yCl.setCellValueFactory(new PropertyValueFactory<>("y"));
        demandsCl.setCellValueFactory(new PropertyValueFactory<>("demands"));

        for (int a = 0; a < FirstSceneController.node.size(); a++) {
            table.getItems().add(FirstSceneController.node.get(a));
        }

        numOfNode.setText(String.valueOf(FirstSceneController.noOfNode));
        maxCap.setText(String.valueOf(FirstSceneController.maxCapacity));
    }

    //Greedy button
    public void greedyOnAction(ActionEvent event) throws IOException {
        Parent nextScene = FXMLLoader.load(getClass().getResource("/views/greedyLoadingView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Obtain the current stage
        Scene scene = new Scene(nextScene);
        stage.setScene(scene); // Set the scene of the obtained stage into the new scene
        stage.show();
    }

    //MCTS button
    public void mctsOnAction(ActionEvent event) throws IOException {
        Parent nextScene = FXMLLoader.load(getClass().getResource("/views/mctsLoadingView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(nextScene);
        stage.setScene(scene);
        stage.show();
    }

    //Basic button
    public void basicOnAction(ActionEvent event) throws IOException {
        Parent nextScene = FXMLLoader.load(getClass().getResource("/views/basicLoadingView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(nextScene);
        stage.setScene(scene);
        stage.show();
    }

    //BestFirstSearch button
    public void bestOnAction(ActionEvent event) throws IOException {
        Parent nextScene = FXMLLoader.load(getClass().getResource("/views/bestFirstSearchLoadingView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(nextScene);
        stage.setScene(scene);
        stage.show();
    }

    //Back button
    public void backOnAction(ActionEvent event) throws IOException {
        Parent nextScene = FXMLLoader.load(getClass().getResource("/views/firstSceneView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(nextScene);
        stage.setScene(scene);
        stage.show();
    }
}
