package org.sq5nry.plaszczka.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class);

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("starting application");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        primaryStage.setTitle("TRX Płaszczka: controller");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        primaryStage.setMinWidth(primaryStage.getWidth()/2);
        primaryStage.setMinHeight(primaryStage.getHeight()/2);
    }

    @Override
    public void stop() throws Exception {
        logger.info("stop: closeCommunicationChannels");
        if (controller != null) {
            controller.closeCommunicationChannels();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
