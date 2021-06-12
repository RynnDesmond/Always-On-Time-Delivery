package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    //Start GUI
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/views/firstSceneView.fxml"));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Always On Time Delivery");
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/res/images/location_icon.jpg")));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
