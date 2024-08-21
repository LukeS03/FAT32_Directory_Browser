import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DirectoryTable {

    final int directoryEntriesCount;
    private ArrayList<DirectoryEntry> directoryEntries; //Raw 32-byte directory table entries.
    private ArrayList<ListDirectoryEntry> listEntries;  //Used to get "human-readable" data from a SFN entry and it's associated LFN entries.

    /**
     * Constructs a directory table from bytes.
     * @param directoryTableBytes The directory table bytes. Length should be a multiple of 32.
     * @throws IllegalArgumentException Thrown if directoryTableBytes' length is not a multiple of 32.
     */
    public DirectoryTable(byte[] directoryTableBytes) throws IllegalArgumentException {
        if(directoryTableBytes.length % 32 != 0) throw new IllegalArgumentException("Directory table should be a multiple of 32-bytes large.");
        directoryEntriesCount = directoryTableBytes.length / 32;
        directoryEntries = new ArrayList<>();
        for(int i = 0; i < directoryEntriesCount; i++) {
            int ptrStart = i * 32; //pointer to where the byte entry starts
            byte[] entryBytes = new byte[32]; // buffer for current bytes
            System.arraycopy(directoryTableBytes, ptrStart, entryBytes, 0, 32);
            byte attributeByte = entryBytes[11]; //get attribute byte
            if(attributeByte == FileAttribute.LONG_FILE_NAME.getValue()) directoryEntries.add(new LongFileName(entryBytes));
            else directoryEntries.add(new DirectoryFileEntry(entryBytes));
        }

        initialiseListEntries();
    }

    /**
     * initialise raw bytes into list entries.
     */
    private void initialiseListEntries() {
        this.listEntries = new ArrayList<>();
        ArrayList<LongFileName> lfnBuffer = new ArrayList<>();
        for(DirectoryEntry e : directoryEntries) {
            if(e.isLfn) {
                lfnBuffer.add((LongFileName) e);
            }
            else {
                listEntries.add(new ListDirectoryEntry((DirectoryFileEntry) e, lfnBuffer));
                lfnBuffer = new ArrayList<>(); //clear LFN buffer.

            }
        }
    }

    /**
     * This file represents a single entry in the table (i.e. a single Standard 8.3 Directory Entry, and it's
     * corresponding Long File Name entries.)
     */
    public class ListDirectoryEntry {
        public ArrayList<LongFileName> longFileNames;
        public DirectoryFileEntry fileEntry;

        public ListDirectoryEntry(DirectoryFileEntry file, ArrayList<LongFileName> longFileNames) {
            this.longFileNames = longFileNames;
            this.fileEntry = file;
        }

        public String getLongFileName() {
            if(longFileNames.isEmpty()) return new String(fileEntry.getShortFileName(), StandardCharsets.UTF_8);
            else return null;
        }

        public String getFileName() {return new String(fileEntry.getShortFileName(), StandardCharsets.UTF_8);}
        public String getExtension() {return new String(fileEntry.getExtension(), StandardCharsets.UTF_8);}
        public LocalDateTime getDateTimeCreated() {return null;}
        public LocalDate     getDateAccessed() {return null;}
        public Long          getSector() {return null;}
        public Long          getFileSize() {return null;}
        public FileAttribute getFileAttribute() {return null;}
    }
}
