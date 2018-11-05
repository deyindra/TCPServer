package com.newrelic.codingchallenge.storage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;


@RunWith(Parameterized.class)
public class SuccessBitStorageTest extends AbstractBitStorageTest {
    private BitStorage storage;
    private int[] setBitIndexs;

    public SuccessBitStorageTest(int length, int[] setBitIndexs) {
        super(length);
        if(this.length==BitStorage.MAX_STORAGE){
            this.storage = new BitStorage();
        }else{
            this.storage = new BitStorage(this.length);
        }
        this.setBitIndexs = setBitIndexs;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {10, new int[]{1,5,3,7}},
                {100, new int[]{17,67,90}},
                {100, new int[]{17,17,67,90}},
                {100, new int[]{17,19,67,90}},
                {4,new int[]{0}},
                {BitStorage.MAX_STORAGE, new int[]{1,7}}
        });
    }

    @Test
    public void successTest() throws InterruptedException {
        for(int v:this.setBitIndexs){
            Thread t = new Thread(new WriteThread(v,this.storage));
            t.start();
            t.join();
        }

        for(int v:this.setBitIndexs){
            Assert.assertTrue(storage.get(v));
            Assert.assertFalse(storage.get(v+1));
        }
    }

    private static class WriteThread implements Runnable{
        private int bitIndex;
        private BitStorage storage;
        private static final Random r = new Random();

        public WriteThread(int bitIndex, BitStorage storage) {
            this.bitIndex = bitIndex;
            this.storage = storage;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(r.nextInt(1000));
                this.storage.set(bitIndex);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
