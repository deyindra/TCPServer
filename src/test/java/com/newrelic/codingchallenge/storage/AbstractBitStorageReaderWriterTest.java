package com.newrelic.codingchallenge.storage;

public abstract class AbstractBitStorageReaderWriterTest {
    protected int concurrencyLevel;
    protected int storageSize;

    protected AbstractBitStorageReaderWriterTest(int concurrencyLevel, int storageSize) {
        this.concurrencyLevel = concurrencyLevel;
        this.storageSize = storageSize;
    }
}
