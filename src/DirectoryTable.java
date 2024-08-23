import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            if(attributeByte == FileAttribute.LONG_FILE_NAME.byteValue) directoryEntries.add(new LongFileName(entryBytes));
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
            //ToDo: placeholder code, fix this up :p
            LocalDate date = this.getDate(DateTimeMethodSpecifier.CREATED);
            LocalDateTime dateTime = LocalDateTime.of(1,1,1,1,1);
            dateTime = dateTime.withYear(date.getYear()).withMonth(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth());
            return dateTime;
        }

        public LocalDate     getDateAccessed() {return null;}
        public LocalDateTime getDateTimeModified() {
            return null;
        }

        public Long getCluster() {
            ByteBuffer clusterAddressBuffer = ByteBuffer.allocate(8);
            clusterAddressBuffer.order(ByteOrder.LITTLE_ENDIAN);
            clusterAddressBuffer.put(0, fileEntry.getLowSectors());
            clusterAddressBuffer.put(2, fileEntry.getHighSectors());
            return clusterAddressBuffer.getLong(0);
        }

        public Long          getFileSize() {return null;}
        public FileAttribute getFileAttribute() {
            return FileAttribute.fromByteValue(this.fileEntry.getAttribute());
        }


        /**
         * ToDo: Refactor this class so that the code from <code>getDateTimeCreated()</code> can be reused for other date and time
         * methods.
         * @param specifier
         * @return
         */
        private LocalDate getDate(DateTimeMethodSpecifier specifier) {

            LocalDate date = LocalDate.of(0,1,1);

            byte[] dateBytes = switch (specifier) {
                case CREATED -> fileEntry.getCreationDate();
                case MODIFIED -> fileEntry.getModificationDate();
                case ACCESSED -> fileEntry.getAccessedDate();
            };

            ByteBuffer dateBuffer = ByteBuffer.allocate(2);
            dateBuffer.put(dateBytes);
            dateBuffer.order(ByteOrder.LITTLE_ENDIAN); //god left me unfinished
            char dateBufferVal = dateBuffer.getChar(0);

            int year  = 1980 + (char) ((dateBufferVal) >>> 9);
            int month = (char) ((dateBufferVal & 0x1E0) >>> 5);
            int day   = (char) (dateBufferVal & 0x1F);

            date = date.withYear(year).withMonth(month).withDayOfMonth(day);

            return date;
        }

        private LocalTime getTime(DateTimeMethodSpecifier specifier) {
            byte[] timeBytes = fileEntry.getCreationTime();
            int hour;
            int minute;
            int second;
            return null;
        }

        private enum DateTimeMethodSpecifier {
            CREATED,
            MODIFIED,
            ACCESSED
        }

    }
}
