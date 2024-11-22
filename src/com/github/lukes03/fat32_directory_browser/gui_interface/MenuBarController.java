package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
                stage.setTitle("FAT32 Directory Browser - " + newOpenFile.getName());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void postInitialisationListenerSetup() {

        //===============================================================
        /* Set up listeners so that invalid partitions are greyed out. */
        //===============================================================

        // Set up partition buttons so that invalid partitions are greyed out.
        BooleanProperty[] validPartitions = model.getValidPartitionsProperty();

        //TODO: Refactor each heading into its' own method.

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

        //===============================================================
        /* Unset the partition radiobuttons when a new file is opened. */
        //===============================================================

        model.getPartitionNumberProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(newValue.intValue() == -1) {
                    partition1Button.setSelected(false);
                    partition2Button.setSelected(false);
                    partition3Button.setSelected(false);
                    partition4Button.setSelected(false);
                }
            }
        });

        //=====================================================
        /* Set up events for clicking the partition buttons. */
        //=====================================================

        partition1Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newPartitionSelection(0);
            }
        });

        partition2Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newPartitionSelection(1);
            }
        });

        partition3Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newPartitionSelection(2);
            }
        });

        partition4Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newPartitionSelection(3);
            }
        });

    }

    public void newPartitionSelection(int selectedPartition) {
        this.model.setNewPartition(selectedPartition);
    }
}
