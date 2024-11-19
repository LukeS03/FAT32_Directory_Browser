package com.github.lukes03.fat32_directory_browser.gui_interface;

import com.github.lukes03.fat32_directory_browser.gui_interface.model.Fat32Model;
import javafx.stage.Stage;

abstract class ComponentController {
    protected Fat32Model model;
    protected Stage      stage;

    /**
     * Used to initialise the stage within the controller after initialize().
     * @param stage
     */
    public void postInitialisationSetup(Stage stage, Fat32Model model) {
        this.stage = stage;
        this.model = model;
        this.postInitialisationListenerSetup();
    }

    public void postInitialisationListenerSetup() {

    }
}
