package com.newrelic.codingchallenge.storage.persist;

import com.newrelic.codingchallenge.Configurable;
import com.newrelic.codingchallenge.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Indranil Dey
 * Responsible for Writing to {@link FileChannel} at a given interval (run every 5 seconds)
 * after it starts
 * @see AutoCloseable
 */
public class ScheduledFileWriter extends Configurable implements AutoCloseable {
    private LinkedBlockingQueue<Integer> queue;
    private FileChannel channel;
    private Timer t;
    private ReentrantReadWriteLock lock;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledFileWriter.class);
    /**
     *
     * @param filePath path of the file where output will be written
     * @throws IllegalArgumentException in case file path is invalid
     */
    public ScheduledFileWriter(ServerConfig config, String filePath) {
        super(config);
        try {
            if (filePath == null || ("").equals(filePath.trim())) {
                throw new IllegalArgumentException("Invalid FilePath");
            }
            filePath = filePath.trim();
            Path p = Paths.get(filePath);
            channel = new FileOutputStream(p.toFile()).getChannel();
            lock = new ReentrantReadWriteLock();
            this.queue = new LinkedBlockingQueue<>();
            t = new Timer("File Writer Timer", true);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    LOGGER.info(String.format("%s kicked off!!", Thread.currentThread().getName()));
                    writeToFileChannel();
                }
            },config.getFileChannelWriteInterval(),config.getFileChannelWriteInterval());
        }catch (FileNotFoundException ex){
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Write to file channel which will be called by {@link Timer}
     * @throws IllegalStateException in case write is unsuccessful
     */
    private void writeToFileChannel() {
        lock.writeLock().lock();
        try {
            if(queue.isEmpty()){
                LOGGER.info("Queue to empty!! Nothing to write to file channel");
            }else{
                while (!queue.isEmpty()) {
                    String number = String.format("%09d", queue.poll());
                    LOGGER.info(String.format("%s is writing to file channel", number));
                    number = String.format("%s\n",number);
                    byte[] array = number.getBytes();
                    ByteBuffer buffer = ByteBuffer.allocate(array.length);
                    buffer.put(array);
                    buffer.flip();
                    channel.write(buffer);
                }
            }
        }catch (IOException ex){
            throw new IllegalStateException(ex);
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * @param number add number to file {@link LinkedBlockingQueue}
     */
    public void add(int number){
        lock.writeLock().lock();
        try {
            queue.offer(number);
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Cancel timer task and drain the remaining content in {@link LinkedBlockingQueue}
     * by calling {@link ScheduledFileWriter#writeToFileChannel()} and finally call the
     * {@link FileChannel#close()}
     * @throws Exception in case of any exception
     */
    @Override
    public void close() throws Exception {
        LOGGER.info("Timer cancelled");
        try {
            t.cancel();
        }finally {
            LOGGER.info("Writing remaining in the file channel");
            writeToFileChannel();
            channel.close();
        }
    }



}
