package com.newrelic.codingchallenge.storage;


import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author Indranil Dey
 * Threadsafe BitSet which will provide set and get Operation on a Bit
 * Based on the current implementation it can store from '0' upto '999,999,999' bits in a thread safe way
 */
class BitStorage {
    private final int length;
    protected static final int MAX_STORAGE = 1000000000;
    private static final String OUTBOUND_ERROR = "Invalid bit index %d";
    private AtomicLongArray bitArray;

    /**
     * Initialize bit Array
     * @param length size should be ranging from 1 to {@link BitStorage#MAX_STORAGE}
     * @throws IllegalArgumentException in case size <=0 or size >{@link BitStorage#MAX_STORAGE}
     */
    public BitStorage(int length) {
        if (length <= 0 || length>MAX_STORAGE)
            throw new IllegalArgumentException("Number of bits can be stored ranges from 0 to 999,999,999");
        this.length = length;
        int intLength = (length + (Long.SIZE-1)) / Long.SIZE;
        bitArray = new AtomicLongArray(intLength);
    }

    /**
     * Initialize Default Storage with size {@link BitStorage#MAX_STORAGE}
     */
    public BitStorage() {
        this(MAX_STORAGE);
    }

    /**
     *
     * @param n turn on a specific nth bit index in threadsafe way
     * @throws ArrayIndexOutOfBoundsException in case bit index outside of range
     */
    public void set(int n) {
        if (n < 0 || n >= length) {
            throw new ArrayIndexOutOfBoundsException(String.format(OUTBOUND_ERROR,n));
        }
        int bit = 1 << n;
        int idx = n >>> 6;
        while (true) {
            long num = bitArray.get(idx);
            long num2 = num | bit;
            if (num == num2 || bitArray.compareAndSet(idx, num, num2))
                return;
        }
    }

    /**
     *
     * @param n return true if a specific nth bit index is on else return false
     * @return true or false
     * @throws ArrayIndexOutOfBoundsException in case bit index outside of range
     */
    public boolean get(int n) {
        if (n < 0 || n >= length)
            throw new ArrayIndexOutOfBoundsException(String.format(OUTBOUND_ERROR,n));
        int bit = 1 << n;
        int idx = n >>> 6;
        if (idx > bitArray.length()) {
            return false;
        } else {
            long num = bitArray.get(idx);
            return (num & bit) != 0;
        }
    }




}
