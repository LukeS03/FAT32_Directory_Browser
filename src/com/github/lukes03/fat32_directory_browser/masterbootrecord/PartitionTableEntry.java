package com.github.lukes03.fat32_directory_browser.masterbootrecord;

/**
 * A class used to extract usable data from a partition table entry.
 */
public class PartitionTableEntry {

    public final PartitionTableEntryBytes bytes;

    public PartitionTableEntry(PartitionTableEntryBytes bytes) {
        this.bytes = bytes;
    }

    /**
     * Gets an integer array representing the CHS values of the start of the partition.
     * @return An integer array [Cylinder, Head, Sector].
     */
    public int[] getStartChsValues() {
        return new int[1];
    }

    /**
     * Gets an integer array representing the CHS values of the end of the partition.
     * @return An integer array [Cylinder, Head, Sector].
     */
    public int[] getEndChsValues() {
        return new int[1];
    }

    /**
     * Gets the start of the partition as represented by an LBA address value.
     * @return The LBA address
     */
    public long getStartLBA() {
        return 0;
    }

    /**
     * Gets the total amount of logical sectors allocated to the partition.
     * @return Duh :p
     */
    public long getTotalSectors() {
        return 0;
    }

    /**
     * Private function used so that getStartChsValues and getEndChsValues don't replicate code.
     * @param isStart true for getStartChsValues, false for getEndChsValues.
     * @return An array of ints equal to [Cylinders, Heads, Sectors].
     */
    private int[] getChsVals(boolean isStart) {
        byte[] chsBytes;
        if(isStart) chsBytes = bytes.getChsStartBytes();
        else chsBytes = bytes.getChsLastBytes();

        return new int[1];
    }
}
