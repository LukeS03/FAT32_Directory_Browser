package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.File;

public class MainInterfaceController extends ComponentController {

    protected File currentFile;
    @FXML protected VBox mainInterfaceRoot;
    @FXML protected FileViewController fileViewComponentController;
    @FXML protected MenuBarController  menuBarComponentController;

    /* <SETUP> -------------------------------------------------------------------------------------------------------*/

    /**
     *
     */
    @FXML protected void initialize() {
        System.out.println("Initialising...");
        fileViewComponentController.postInitialisationSetup(stage, model);
        menuBarComponentController.postInitialisationSetup(stage, model);

    }

}
