import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public ArrayList<ListDirectoryEntry> getEntries() {
        return this.listEntries;
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
            return null;
        }

        public String getShortFileName() {return new String(fileEntry.getShortFileName(), StandardCharsets.UTF_8);}

        public String getFileName() {return new String(fileEntry.getShortFileName(), StandardCharsets.UTF_8);}
        public String getExtension() {return new String(fileEntry.getExtension(), StandardCharsets.UTF_8);}

        public LocalDateTime getDateTimeCreated() {
            LocalDateTime dateTime = LocalDateTime.of(1,1,1,1,1);

            byte[] dateBytes = fileEntry.getCreationDate();
            ByteBuffer dateBuffer = ByteBuffer.allocate(2);
            dateBuffer.put(dateBytes);
            dateBuffer.order(ByteOrder.LITTLE_ENDIAN); //god left me unfinished
            char dateBufferVal = dateBuffer.getChar(0);

            int year  = 1980 + (char) ((dateBufferVal) >>> 9);
            int month = (char) ((dateBufferVal & 0x1E0) >>> 5);
            int day   = (char) (dateBufferVal & 0x1F);

            dateTime = dateTime.withYear(year).withMonth(month).withDayOfMonth(day);


            byte[] timeBytes = fileEntry.getCreationTime();
            int hour;
            int minute;
            int second;

            return dateTime;
        }
        public LocalDate     getDateAccessed() {return null;}
        public LocalDateTime getDateTimeModified() {
            LocalDateTime modifiedDateTime;

            byte[] modificationDateBytes = fileEntry.getModificationDate();
            byte[] modificationTimeBytes = fileEntry.getModificationTime();

            byte year = (byte) (modificationDateBytes[0] & 0xFE >> 1);
            byte month;
            byte day;
            byte hour;
            byte minute;
            byte second;

            return null;
        }
        public Long          getSector() {return null;}
        public Long          getFileSize() {return null;}
        public FileAttribute getFileAttribute() {
            return FileAttribute.fromByteValue(this.fileEntry.getAttribute());
        }
    }
}
