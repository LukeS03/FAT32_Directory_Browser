package com.github.lukes03.fat32_directory_browser.masterbootrecord;

public class PartitionTableEntry {

    public final PartitionTableEntryBytes bytes;

    public PartitionTableEntry(PartitionTableEntryBytes bytes) {
        this.bytes = bytes;
    }

    public int[] getStartChsValues() {
        return new int[1];
    }

    public int[] getEndChsValues() {
        return new int[1];
    }

    public long getStartLBA() {
        return 0;
    }

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
