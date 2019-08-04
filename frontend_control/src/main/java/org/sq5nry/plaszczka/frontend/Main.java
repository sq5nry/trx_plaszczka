package org.sq5nry.plaszczka.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("TRX Płaszczka: controller");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        //primaryStage.setFullScreen(true);
    }

    @Override
    public void stop() throws Exception {
        controller.closeCommunicationChannels();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
