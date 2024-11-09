package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("MainInterface.fxml"));
        Parent sceneRoot = sceneLoader.load();
        Scene scene = new Scene(sceneRoot);
        stage.setTitle("FAT32 Directory Browser");

        Fat32Model model = new Fat32Model();

        //pass stage to controller.
        MainInterfaceController interfaceController = sceneLoader.getController();
        interfaceController.postInitialisationSetup(stage, model);

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}