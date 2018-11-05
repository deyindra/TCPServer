package com.newrelic.codingchallenge.storage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class SuccessBitStorageReaderWriterTest extends AbstractBitStorageReaderWriterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuccessBitStorageReaderWriterTest.class);
    private int[] numbers;
    private int expectedUniqValue;

    public SuccessBitStorageReaderWriterTest(int concurrencyLevel, int storageSize, int[] numbers, int expectedUniqValue) {
        super(concurrencyLevel, storageSize);
        this.numbers = numbers;
        this.expectedUniqValue = expectedUniqValue;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4, 10, new int[]{0,1,2,3,4,5,6,7,8,9},10},
                {4, 10, new int[]{1,7,7,7},2},
                {4, 10, new int[]{1,1,7,7},2},
                {BitStorageReaderWriter.DEFAULT_CONCURRENCY_LEVEL, BitStorage.MAX_STORAGE,new int[]{1,2},2}
        });
    }

    @Test
    public void successStorageWriterTest() throws InterruptedException {
        BitStorageReaderWriter writer;
        if(this.concurrencyLevel==BitStorageReaderWriter.DEFAULT_CONCURRENCY_LEVEL && this.storageSize==BitStorage.MAX_STORAGE){
            writer = new BitStorageReaderWriter();
        }else {
            writer =new BitStorageReaderWriter(concurrencyLevel, storageSize);
        }
        for(int v:this.numbers){
            Thread t = new Thread(new SuccessBitStorageReaderWriterTest.Writer(writer,v));
            t.start();
            t.join();
        }

        Assert.assertEquals(writer.getStats().getTotalCount(),expectedUniqValue);
    }



    private static class Writer implements  Runnable{
        private BitStorageReaderWriter readerWriter;
        private static final Random r = new Random();
        private int number;

        Writer(BitStorageReaderWriter readerWriter, int number) {
            this.readerWriter = readerWriter;
            this.number = number;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(r.nextInt(1000));
                this.readerWriter.write(number);
                LOGGER.info(readerWriter.getStats().toString());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
