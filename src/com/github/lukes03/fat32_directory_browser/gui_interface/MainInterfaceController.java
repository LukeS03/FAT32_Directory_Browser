package com.github.lukes03.fat32_directory_browser.gui_interface;

import com.github.lukes03.fat32_directory_browser.gui_interface.model.Fat32Model;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class MainInterfaceController extends ComponentController {

    protected File currentFile;
    @FXML protected VBox mainInterfaceRoot;
    @FXML protected FileViewController fileViewComponentController;
    @FXML protected MenuBarController  menuBarComponentController;

    /* <SETUP> -------------------------------------------------------------------------------------------------------*/

    @Override
    public void postInitialisationSetup(Stage stage, Fat32Model model) {
        this.stage = stage;
        this.model = model;
        fileViewComponentController.postInitialisationSetup(stage, model);
        menuBarComponentController.postInitialisationSetup(stage, model);
    }

}
