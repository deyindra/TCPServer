package com.newrelic.codingchallenge.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Indranil Dey
 * The class responsible for wrtring Integer output to {@link BitStorage}. Support Thread safety
 *
 */
public class BitStorageReaderWriter {
    //Supports concurrency level how many thread can work at a given moment in parallel
    static final int DEFAULT_CONCURRENCY_LEVEL = Long.SIZE/4;
    /**
     * Underlying {@link BitStorage} class
     */
    private BitStorage storage;

    private ReadWriteHandler[] handlers;
    private static final Logger LOGGER = LoggerFactory.getLogger(BitStorageReaderWriter.class);

    /**
     * @param concurrencyLevel number of thread can work on this instance.
     * @param storageSize size of the {@link BitStorage}
     * @throws IllegalArgumentException in case of concurrencyLevel is less than or equal to 0
     */

    public BitStorageReaderWriter(int concurrencyLevel, int storageSize) {
        if(concurrencyLevel<=0){
            throw new IllegalArgumentException("Invalid Argument for Concurrency level");
        }
        this.storage = new BitStorage(storageSize);
        handlers = new ReadWriteHandler[concurrencyLevel];
        for(int count=0;count<concurrencyLevel;count++){
            handlers[count] = new ReadWriteHandler();
        }
    }

    /**
     * Default constructor with {@link BitStorageReaderWriter#DEFAULT_CONCURRENCY_LEVEL} and
     * {@link BitStorage#MAX_STORAGE}
     */
    public BitStorageReaderWriter(){
        this(DEFAULT_CONCURRENCY_LEVEL,BitStorage.MAX_STORAGE);
    }

    /**
     * @param number write number to underlying {@link BitStorage} Also it will keep track of underlying Counter
     * @throws  IllegalArgumentException in case number of -ve.
     */
    public void write(final int number){
        if(number<0){
            throw new IllegalArgumentException("Invalid number it can only be positive");
        }
        int index = number%handlers.length;
        handlers[index].lock.writeLock().lock();
        try{
            handlers[index].previousUniqCount.add(handlers[index].currentUniqCount.sum());
            handlers[index].previousDuplicateCount.add(handlers[index].currentDuplicateCount.sum());

            if(storage.get(number)){
                LOGGER.info("Duplicate Number found {}", number);
                handlers[index].currentDuplicateCount.add(1L);
                LOGGER.info("Previous Duplicate Count {}", handlers[index].previousDuplicateCount.sum());
                LOGGER.info("Current Duplicate Count {}", handlers[index].currentDuplicateCount.sum());
            }else{
                storage.set(number);
                handlers[index].currentUniqCount.add(1L);
                LOGGER.info("Previous unique Count {}", handlers[index].previousUniqCount.sum());
                LOGGER.info("Current unique Count {}", handlers[index].currentUniqCount.sum());
            }
        }finally {
            handlers[index].lock.writeLock().unlock();
        }
    }

    /**
     *
     * @return {@link BitStorageReaderWriter.Statistics} instance. This will return the current status of unique/duplicate
     * counts to client
     *
     */
    public Statistics getStats(){
        for(ReadWriteHandler handler:handlers){
            handler.lock.readLock().lock();
        }
        try{
            long previousDuplicateCount = 0;
            long currentDuplicateCount = 0;
            long previousUniqCount=0;
            long currentUniqCount=0;

            for(ReadWriteHandler handler:handlers){
                previousDuplicateCount+=handler.previousDuplicateCount.sum();
                currentDuplicateCount+=handler.currentDuplicateCount.sum();
                previousUniqCount+=handler.previousUniqCount.sum();
                currentUniqCount+=handler.currentUniqCount.sum();
            }
            return new Statistics(currentUniqCount,
                    (currentDuplicateCount-previousDuplicateCount),(currentUniqCount-previousUniqCount));


        }finally {
            for(ReadWriteHandler handler:handlers){
                handler.lock.readLock().unlock();
            }
        }

    }


    /**
     * Private Inner class which will provide {@link ReentrantReadWriteLock} based on which bucket the input number
     * belongs to. It also keep track of how many duplicate/unique number being added
     */
    private static final class ReadWriteHandler {
        private ReentrantReadWriteLock lock;
        private LongAdder currentUniqCount;
        private LongAdder previousUniqCount;
        private LongAdder currentDuplicateCount;
        private LongAdder previousDuplicateCount;

        private ReadWriteHandler() {
            this.lock = new ReentrantReadWriteLock();
            this.currentUniqCount = new LongAdder();
            this.previousUniqCount = new LongAdder();
            this.currentDuplicateCount = new LongAdder();
            this.previousDuplicateCount = new LongAdder();
        }
   }

    /**
     * A class which is responsible for reporting how many unique/duplicate numbers being added since last reporting and
     * how many unique number being entered to the system so far...
     */
   public static final class Statistics{
        private final long totalCount;
        private final long incrementalDuplicateCount;
        private final long incrementalUniqCount;

        private Statistics(long totalCount, long incrementalDuplicateCount, long incrementalUniqCount) {
           this.totalCount = totalCount;
           this.incrementalDuplicateCount = incrementalDuplicateCount;
           this.incrementalUniqCount = incrementalUniqCount;
       }

       public long getTotalCount() {
           return totalCount;
       }
        @Override
        public String toString() {
            return "" + String.format("Received %d unique numbers, %d duplicates. ",
                    incrementalUniqCount, incrementalDuplicateCount) +
                    String.format("Unique total: %d", totalCount);
        }
    }



}
