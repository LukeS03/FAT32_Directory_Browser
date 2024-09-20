import com.github.lukes03.fat32_directory_browser.FileSystem;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.fat32.DirectoryTableEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String filePath = "/home/luke/IdeaProjects/FAT32 Directory Entry Browser/fatTestImg/diskimgmount.img";
        FileSystem fileSystem = null;
        try {
            fileSystem = new FileSystem(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        fileSystem.setBlockSize(1024);
        fileSystem.setCurrentPartitionIndex(0);
        DirectoryTable root;
        try {
            root = fileSystem.getRootDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        root.getEntries();
    }
}