package com.github.lukes03.fat32_directory_browser.libfat32.fat32;

import java.util.ArrayList;

public class DirectoryTable {

    final int directoryEntriesCount;
    private ArrayList<DirectoryEntryBytes> directoryEntries; //Raw 32-byte directory table entries.
    private ArrayList<DirectoryTableEntry> listEntries;  //Used to get "human-readable" data from a SFN entry and it's associated LFN entries.

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
            if(attributeByte == FileAttribute.LONG_FILE_NAME.byteValue) directoryEntries.add(new LongFileNameBytes(entryBytes));
            else directoryEntries.add(new DirectoryFileEntryBytes(entryBytes));
        }

        initialiseListEntries();
    }

    /**
     * initialise raw bytes into list entries.
     */
    private void initialiseListEntries() {
        this.listEntries = new ArrayList<>();
        ArrayList<LongFileNameBytes> lfnBuffer = new ArrayList<>();
        for(DirectoryEntryBytes e : directoryEntries) {
            if(e.isLfn) {
                lfnBuffer.add((LongFileNameBytes) e);
            }
            else {
                listEntries.add(new DirectoryTableEntry((DirectoryFileEntryBytes) e, lfnBuffer));
                lfnBuffer = new ArrayList<>(); //clear LFN buffer.
            }
        }
    }

    public ArrayList<DirectoryTableEntry> getEntries() {
        return this.listEntries;
    }
}
