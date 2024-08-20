import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DirectoryTable {

    /**
     * This file represents a single entry in the table (i.e. a single Standard 8.3 Directory Entry, and it's
     * corresponding Long File Name entries.)
     */
    public class FileEntry {
        public ArrayList<LongFileName> longFileNames;
        public DirectoryFileEntry fileEntry;

        public String        getLongFileName() {return null;}
        public String        getFileName() {return null;}
        public String        getExtension() {return null;}
        public LocalDateTime getDateTimeCreated() {return null;}
        public LocalDate     getDateAccessed() {return null;}
        public Long          getSector() {return null;}
        public Long          getFileSize() {return null;}
        public FileAttribute getFileAttribute() {return null;}
    }
}
