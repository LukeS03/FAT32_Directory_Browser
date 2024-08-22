import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        File theFile = new File("/home/luke/IdeaProjects/FAT32 Directory Entry Browser/testbytestring");
        byte[] testBytes;
        try {
            testBytes = Files.readAllBytes(theFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*DirectoryFileEntry testFileEntry = new DirectoryFileEntry(testBytes);
        String testName = new String(testFileEntry.getShortFileName(), StandardCharsets.UTF_8);*/
        DirectoryTable rootTable = new DirectoryTable(testBytes);
        DirectoryTable.ListDirectoryEntry dirEntry = rootTable.getEntries().get(0);
        System.out.println("File Name: " + dirEntry.getFileName());
        System.out.println("Short File Name: " + dirEntry.getShortFileName());
        System.out.println("Long File Name: " + dirEntry.getLongFileName());
        System.out.println("Test Over");
    }
}