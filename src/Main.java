import com.github.lukes03.fat32_directory_browser.libfat32.FileSystem;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTable;
import com.github.lukes03.fat32_directory_browser.libfat32.fat32.DirectoryTableEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        String filePath = "fatTestImg/diskimgmount.img";
        FileSystem fileSystem = null;
        try {
            fileSystem = new FileSystem(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        fileSystem.setCurrentPartitionIndex(0);
        DirectoryTable root;
        try {
            root = fileSystem.getRootDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DirectoryTableEntry fileEntry = root.getEntries().get(3);
        byte[] txtBytes = fileSystem.getFileBytes(fileEntry, 32);
        Path fileOutPath = Paths.get("fatTestImg/newfile.txt");
        Files.write(fileOutPath, txtBytes);
    }
}