package com.github.lukes03.fat32_directory_browser.fat32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

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
        private final ArrayList<LongFileName> longFileNames;
        private final DirectoryFileEntry fileEntry;
        private String longFileName;

        public ListDirectoryEntry(DirectoryFileEntry file, ArrayList<LongFileName> longFileNames) {
            this.longFileNames = longFileNames;
            this.fileEntry = file;
            parseLongFileName();
        }

        public String getLongFileName() {
            return longFileName;
        }

        public String getFileName() {
            if(longFileNames.isEmpty()) return getShortFileName();
            else return getLongFileName();
        }

        public String getShortFileName() {return new String(fileEntry.getShortFileName(), StandardCharsets.UTF_8);}

        public String getExtension() {return new String(fileEntry.getExtension(), StandardCharsets.UTF_8);}

        public Long getCluster() {
            ByteBuffer clusterAddressBuffer = ByteBuffer.allocate(8);
            clusterAddressBuffer.order(ByteOrder.LITTLE_ENDIAN);
            clusterAddressBuffer.put(0, fileEntry.getLowSectors());
            clusterAddressBuffer.put(2, fileEntry.getHighSectors());
            return clusterAddressBuffer.getLong(0);
        }

        public long getFileSize() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.put(fileEntry.getFileSize());
            return byteBuffer.getLong(0);
        }

        public FileAttribute getFileAttribute() {
            return FileAttribute.fromByteValue(this.fileEntry.getAttribute());
        }

        public LocalDateTime getDateTimeCreated() {
            LocalDate date = this.getDate(DateTimeMethodSpecifier.CREATED);
            LocalTime time = this.getTime(DateTimeMethodSpecifier.CREATED);
            LocalDateTime dateTime = LocalDateTime.of(1,1,1,1,1);
            dateTime = dateTime.withYear(date.getYear()).withMonth(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth());
            dateTime = dateTime.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(time.getSecond()).withNano(time.getNano());
            return dateTime;
        }

        public LocalDateTime getDateTimeModified() {
            LocalDate date = this.getDate(DateTimeMethodSpecifier.MODIFIED);
            LocalTime time = this.getTime(DateTimeMethodSpecifier.MODIFIED);
            LocalDateTime dateTime = LocalDateTime.of(1,1,1,1,1);
            dateTime = dateTime.withYear(date.getYear()).withMonth(date.getMonthValue()).withDayOfMonth(date.getDayOfMonth());
            dateTime = dateTime.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(time.getSecond());
            return dateTime;
        }

        public LocalDate getDateAccessed() {
            return this.getDate(DateTimeMethodSpecifier.ACCESSED);
        }

        private void parseLongFileName() {
            if(longFileNames.isEmpty()) return; //longFileName is null if there are no LFN entries.

            int byteBufferLen = 26 * longFileNames.size() + 2;

            //create a buffer for the LFN bytes and read the LFN bytes into it.
            ByteBuffer lfnBytesBuffer = ByteBuffer.allocate(byteBufferLen);
            lfnBytesBuffer.order(ByteOrder.LITTLE_ENDIAN);

            //Has to be reversed because for some reason FAT32 likes to store LFN entries in reverse order?? Very strange.
            //TODO: This should probably be replaced with something that reads the index of each entry, but this will do for now.
            Collections.reverse(longFileNames);
            for(LongFileName l : longFileNames) {
                lfnBytesBuffer.put(l.getLongFileNameBytes());
            }
            lfnBytesBuffer.rewind();
            char[] trimmedCharBytes = new char[byteBufferLen/2];
            int charLen = 0;
            for(int i = 0; i <= byteBufferLen/2; i++) {
                char charBuffer = lfnBytesBuffer.getChar();
                if(charBuffer != 0xFF && charBuffer != 0x00) {
                    trimmedCharBytes[i] = charBuffer;
                    charLen++;
                }
                else break;
            }

            char[] trimmedLongFileName = new char[charLen];
            System.arraycopy(trimmedCharBytes, 0, trimmedLongFileName, 0, charLen);

            longFileName = new String(trimmedLongFileName);
        }

        /**
         * Gets the date that a file was created, modified or accessed.
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

        /***
         * Get time that a file was accessed or created
         * @param specifier Must be CREATED or MODIFIED. FAT32 does not support time accessed, and an exception will be thrown.
         * @return
         */
        private LocalTime getTime(DateTimeMethodSpecifier specifier) {
            if(specifier == DateTimeMethodSpecifier.ACCESSED) throw new IllegalArgumentException("Illegal getTime parameter. Accessed time is not stored in FAT32 format.");

            byte[] timeBytes = switch (specifier) {
                case CREATED -> fileEntry.getCreationTime();
                case MODIFIED -> fileEntry.getModificationTime();
                case ACCESSED -> null;
            };

            LocalTime time = LocalTime.of(0,0,0);

            ByteBuffer timeBuffer = ByteBuffer.allocate(2);
            timeBuffer.order(ByteOrder.LITTLE_ENDIAN);
            timeBuffer.put(timeBytes);
            char timeBufferVal = timeBuffer.getChar(0);

            int hour = (char) (timeBufferVal >>> 11);
            int minute = (char) ((timeBufferVal & 0x780) >>> 5);
            int second = (char) ((timeBufferVal & 0x1F) * 2);

            time = time.withHour(hour).withMinute(minute).withSecond(second);

            if(specifier == DateTimeMethodSpecifier.CREATED) {
                byte creationTenthSeconds = fileEntry.getCreationTenthSeconds();
                int creationNanoSeconds = creationTenthSeconds * 100000000;
                time = time.withNano(creationNanoSeconds);
            }

            return time;
        }

        private enum DateTimeMethodSpecifier {
            CREATED,
            MODIFIED,
            ACCESSED
        }

    }
}
