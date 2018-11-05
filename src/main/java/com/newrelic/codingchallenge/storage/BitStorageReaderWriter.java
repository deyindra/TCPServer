package com.newrelic.codingchallenge.storage;

import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BitStorageReaderWriter {
    private static final int DEFAULT_CONCURRENCY_LEVEL = Long.SIZE/4;
    private BitStorage storage;
    private ReadWriteHandler[] handlers;

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

    public BitStorageReaderWriter(){
        this(DEFAULT_CONCURRENCY_LEVEL,BitStorage.MAX_STORAGE);
    }

    public void write(final int number){
        int index = number%handlers.length;
        handlers[index].lock.writeLock().lock();
        try{
            handlers[index].previoiusUniqCount = handlers[index].currentUniqCount;
            handlers[index].previousDuplicateCount = handlers[index].currentDuplicateCount;

            if(storage.get(number)){
                handlers[index].currentDuplicateCount.add(1L);
            }else{
                storage.set(number);
                handlers[index].currentUniqCount.add(1L);
            }
        }finally {
            handlers[index].lock.writeLock().unlock();
        }
    }

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
                previousUniqCount+=handler.previoiusUniqCount.sum();
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






    private static final class ReadWriteHandler {
        private ReentrantReadWriteLock lock;
        private LongAdder currentUniqCount;
        private LongAdder previoiusUniqCount;
        private LongAdder currentDuplicateCount;
        private LongAdder previousDuplicateCount;

        public ReadWriteHandler() {
            this.lock = new ReentrantReadWriteLock();
            this.currentUniqCount = new LongAdder();
            this.previoiusUniqCount = new LongAdder();
            this.currentDuplicateCount = new LongAdder();
            this.previousDuplicateCount = new LongAdder();
        }
   }

   public static final class Statistics{
        private final long totalCount;
        private final long incrementalDuplicateCount;
        private final long incrementalUniqCount;

       public Statistics(long totalCount, long incrementalDuplicateCount, long incrementalUniqCount) {
           this.totalCount = totalCount;
           this.incrementalDuplicateCount = incrementalDuplicateCount;
           this.incrementalUniqCount = incrementalUniqCount;
       }

       public long getTotalCount() {
           return totalCount;
       }

       public long getIncrementalDuplicateCount() {
           return incrementalDuplicateCount;
       }

       public long getIncrementalUniqCount() {
           return incrementalUniqCount;
       }


   }



}
