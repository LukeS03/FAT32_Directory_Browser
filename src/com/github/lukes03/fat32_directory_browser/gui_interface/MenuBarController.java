package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MenuBarController extends ComponentController {

    /**
     * Run when the option <b>File > Open File System</b> is clicked. Opens a file chooser to choose a binary
     * image of a FAT32 file system.
     */
    @FXML
    protected void File_openFileSystem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open FAT32 file system binary image...");
        File newOpenFile = fileChooser.showOpenDialog(stage);

    }
}
