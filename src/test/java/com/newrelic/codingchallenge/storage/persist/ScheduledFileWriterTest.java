package com.newrelic.codingchallenge.storage.persist;

import com.newrelic.codingchallenge.rule.ExceptionLoggingRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;

public class ScheduledFileWriterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Rule
    public ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule public ExpectedException expectedException = ExpectedException.none();


    @Test
    public void testSuccessScheduler() throws Exception {
        File tempFile = testFolder.newFile("file.txt");
        Deque<String> deque = new LinkedList<>();
        ScheduledFileWriter writer = new ScheduledFileWriter(tempFile.getPath());
        for(int i=0;i<10;i++){
            int number = i;
            Thread t = new Thread(() -> {
                try {
                    writer.add(number);
                    deque.offer(String.format("%09d", number));
                    Thread.sleep(1000);
                }catch (InterruptedException ex){
                    throw new RuntimeException(ex);
                }

            });
            t.start();
            t.join();
        }
        writer.add(11);
        deque.offer(String.format("%09d", 11));
        writer.add(12);
        deque.offer(String.format("%09d", 12));
        writer.close();

        for(String v: Files.readAllLines(tempFile.toPath())){
            Assert.assertEquals(v, deque.poll());
        }


    }


    @Test
    public void testSuccessSchedulerWithEmptyQueue() throws Exception {
        File tempFile = testFolder.newFile("file1.txt");
        ScheduledFileWriter writer = new ScheduledFileWriter(tempFile.getPath());
        Thread.sleep(10000);
        writer.close();
    }

    @Test
    public void testFailureScheduledFileWriterWithNull(){
        expectedException.expect(IllegalArgumentException.class);
        new ScheduledFileWriter(null);
    }

    @Test
    public void testFailureScheduledFileWriterWithEmptyString(){
        expectedException.expect(IllegalArgumentException.class);
        new ScheduledFileWriter("");
    }

    @Test
    public void testFailureScheduledFileWriterWithEmptyStringWithSpaces(){
        expectedException.expect(IllegalArgumentException.class);
        new ScheduledFileWriter(" ");
    }

    @Test
    public void testFailureScheduledFileWriterWithFolderPath(){
        expectedException.expect(IllegalArgumentException.class);
        new ScheduledFileWriter(System.getProperty("user.home"));
    }

}
