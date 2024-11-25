package com.github.lukes03.fat32_directory_browser.gui_interface;

import com.github.lukes03.fat32_directory_browser.gui_interface.model.Fat32Model;
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
        fileViewTable.setShowRoot(false);
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
            root.getChildren().add(initNewTreeItem(f));

        }

        fileViewTable.setRoot(root);

    }


    /**
     * Initialise a new FileModel tree item with special method overrides to load items on command and not on initialisation.
     * <br> This code is directly derived from the example listed in the Oracle
     * <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TreeItem.html">TreeItem</a> documentation.
     *
     * @param f
     * @return
     */
    private TreeItem<FileModel> initNewTreeItem(FileModel f) {
        return new TreeItem<FileModel>(f) {

            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf     = true;

            //ToDo: This code will need re-jigging to deal with the fact that it will probably display empty directories
            // as having children even after loading their children.

            // Override getChildren method so that it acquires the child TreeItems only once they are needed.
            @Override
            public ObservableList<TreeItem<FileModel>> getChildren() {
                // Initialise children when needed.
                if (isFirstTimeChildren && f.getIsDirectory()) {
                    if (f.getIsDirectory()) {

                        //get a list of children files.
                        ObservableList<FileModel> childFiles = null;
                        try {
                            childFiles = model.getDirectoryChildren(f);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        //initialise child files
                        for (FileModel m : childFiles) {
                            if (m.isValidDirectoryChild())
                                super.getChildren().add(initNewTreeItem(m));
                        }

                        //calling this method also tells us if the tree item is a leaf or not.
                        isLeaf = super.getChildren().isEmpty();
                    }
                }

                if(isFirstTimeChildren) this.isFirstTimeChildren = false;

                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if(isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    isLeaf = !f.getIsDirectory();
                }
                return isLeaf;
            }
        };
    }
}

