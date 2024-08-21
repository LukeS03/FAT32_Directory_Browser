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
        DirectoryFileEntry testFileEntry = new DirectoryFileEntry(testBytes);
        String testName = new String(testFileEntry.getShortFileName(), StandardCharsets.UTF_8);
        System.out.println("Da Baby");
    }
}