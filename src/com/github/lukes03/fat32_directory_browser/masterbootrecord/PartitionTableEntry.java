package com.github.lukes03.fat32_directory_browser.masterbootrecord;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        return getChsVals(true);
    }

    /**
     * Gets an integer array representing the CHS values of the end of the partition.
     * @return An integer array [Cylinder, Head, Sector].
     */
    public int[] getEndChsValues() {
        return getChsVals(false);
    }

    /**
     * Gets the start of the partition as represented by an LBA address value.
     * @return The LBA address
     */
    public long getStartLBA() {
        byte[] lbaBuf = bytes.getStartLbaAddress();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(0, lbaBuf);
        return byteBuffer.getLong();
    }

    /**
     * Gets the total amount of logical sectors allocated to the partition.
     * @return Duh :p
     */
    public long getTotalSectors() {
        byte[] lbaBuf = bytes.getNumSectors();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(0, lbaBuf);
        return byteBuffer.getLong();
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

        int chsBytesCombined; //convert the array of chs bytes so that they can be masked and shifted and whatnot.
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(0, chsBytes);

        chsBytesCombined = byteBuffer.getInt();

        int heads     = (chsBytesCombined & 0xFF000000) >> 24;
        int cylinders = (chsBytesCombined & 0xFC0000)   >> 18;
        int sectors   = (chsBytesCombined & 0x3FF00)    >> 8;

        int[] chsVals = {heads, cylinders, sectors};

        return chsVals;
    }

    /**
     * Check if the partition is a valid partition.
     * @return True if valid partition.
     */
    public boolean isValidPartition() {
        if(this.getTotalSectors() == 0 ) return false;
        return true;
    }
}
