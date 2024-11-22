package com.github.lukes03.fat32_directory_browser.gui_interface;

import com.github.lukes03.fat32_directory_browser.gui_interface.model.FileModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

import java.io.IOException;

public class FileViewController extends ComponentController {
    @FXML private TreeTableView<Object> fileViewTable;
    @FXML private TreeTableColumn<FileModel, String> fileNameColumn;
    @FXML private TreeTableColumn<FileModel, String> fileExtensionColumn;
    @FXML private TreeTableColumn<FileModel, Long>   fileSizeColumn;

    private TreeItem root;

    private final Label noFileLoadedPlaceholderText = new Label("Please open a FAT32 File System binary image and select a partition...");
    private final Label fileLoadedPlaceholderText   = new Label ("Please select a partition from the menu bar to continue...");


    @FXML protected void initialize() {
        fileViewTable.setPlaceholder(noFileLoadedPlaceholderText);
        fileViewTable.setShowRoot(false);
        fileNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileName"));
        fileExtensionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("extension"));
        fileSizeColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileSizeBytes"));

    }

    public void postInitialisationListenerSetup() {
        //fileViewTable.setShowRoot(false);
        model.getPartitionNumberProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                fileViewTable.setPlaceholder(fileLoadedPlaceholderText);
                if(newValue.intValue() != -1) loadNewPartition();
                else fileViewTable.setRoot(null);
            }
        });

        model.getFilePathProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                fileViewTable.setPlaceholder(fileLoadedPlaceholderText);
            }
        });


    }

    /**
     * Called when a new partition is loaded.
     */
    private void loadNewPartition() {
        ObservableList<FileModel> rootEntries = null;

        try {
            rootEntries = model.getRootDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        root = new TreeItem(null);

        for (FileModel f : rootEntries) {
            addTreeItem(f, root);
        }

        fileViewTable.setRoot(root);

    }

    private void addTreeItem(FileModel f, TreeItem p) {
        //initialise a silly little new tree item !!! :3c
        TreeItem treeItem = new TreeItem(f);

        if(f.getIsDirectory()) {
            ObservableList<FileModel> childFiles = null;

            //populate the directory with it's children.
            //this will really need fixing in the future, this sounds like a pain in the ass if you have a
            //big file system.
            //TODO: fix this with the example provided by the oracle docs.
            //TODO: Also, improve error handling. I have the feeling that this will crash the entire program if it
            // encounters even a single mucked up directory entry. Which isn't great for a recovery software.
            try {
                childFiles = model.getDirectoryChildren(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            for(FileModel m : childFiles) {
                if(m.isValidDirectoryChild())
                    addTreeItem(m, treeItem);
            }
        }
        p.getChildren().add(treeItem);
    }
}

