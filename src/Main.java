import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        File theFile = new File("/home/luke/IdeaProjects/FAT32 Directory Entry Browser/fatTestImg/testDir.img");
        byte[] testBytes;
        try {
            testBytes = Files.readAllBytes(theFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DirectoryTable rootTable = new DirectoryTable(testBytes);
        for(DirectoryTable.ListDirectoryEntry dirEntry : rootTable.getEntries()) {
            System.out.println("\nFile Name: " + dirEntry.getFileName());
            System.out.println("Short File Name: " + dirEntry.getShortFileName());
            System.out.println("Long File Name: " + dirEntry.getLongFileName());
            System.out.println("Extension: " + dirEntry.getExtension());
            System.out.println("Attribute: " + dirEntry.getFileAttribute().attributeName);
            System.out.println("Creation Date & Time: " + dirEntry.getDateTimeCreated().toString());
            System.out.println("Last Modified Date & Time: " + dirEntry.getDateTimeModified().toString());
            System.out.println("Last Accessed Date: " + dirEntry.getDateAccessed().toString());
            System.out.println("File Size: " + dirEntry.getFileSize());
            System.out.println("First Cluster Address:" + dirEntry.getCluster().toString());

        }
        System.out.println("Test Over");
    }
}