package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class MenuBarController extends ComponentController {

    @FXML RadioMenuItem partition1Button;
    @FXML RadioMenuItem partition2Button;
    @FXML RadioMenuItem partition3Button;
    @FXML RadioMenuItem partition4Button;

    /**
     * Run when the option <b>File > Open File System</b> is clicked. Opens a file chooser to choose a binary
     * image of a FAT32 file system.
     */
    @FXML
    protected void File_openFileSystem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open FAT32 file system binary image...");
        File newOpenFile = fileChooser.showOpenDialog(stage);
        if(newOpenFile != null) {
            try {
                model.openNewFile(newOpenFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void postInitialisationListenerSetup() {

        /* SET UP THE MENU BUTTON LISTENERS. */

        // Set up partition buttons so that invalid partitions are greyed out.
        BooleanProperty[] validPartitions = model.getValidPartitionsProperty();

        //TODO: Find a way to refactor this pile of garbage.

        // bruh
        validPartitions[0].addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                partition1Button.setDisable(!newValue);
        }});

        validPartitions[1].addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                partition2Button.setDisable(newValue);
        }});

        validPartitions[2].addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                partition3Button.setDisable(newValue);
        }});

        validPartitions[3].addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                partition4Button.setDisable(newValue);
        }});
    }
}
