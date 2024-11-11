package com.github.lukes03.fat32_directory_browser.gui_interface;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;

public class FileViewController extends ComponentController {
    @FXML private TreeTableView<Object> fileViewTable;

    @FXML protected void initialize() {
        fileViewTable.setPlaceholder(new Label("Please open a FAT32 File System binary image and select a partition..."));
    }
}
