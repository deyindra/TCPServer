package com.newrelic.codingchallenge.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Config Object which will return {@link Config} based on the user's provided settings.
 */
public final class ServerConfig {
    public static final String CONFIG_FILE_KEY = "config.path";
    private Config config;

    /**
     * Check if the {@link ServerConfig#CONFIG_FILE_KEY} set in the System property if so, it will load the config
     * from the environmental variable else it will load from the class path
     * @throws IllegalArgumentException in case the file path present in {@link ServerConfig#CONFIG_FILE_KEY} is invalid.
     */
    public ServerConfig(){
        String filePath = System.getProperty(CONFIG_FILE_KEY);
        if(filePath==null || ("").equals(filePath.trim())){
            config = ConfigFactory.load("application");
        }else{
            Path p = Paths.get(filePath);
            if(Files.exists(p) && Files.isRegularFile(p)){
                config = ConfigFactory.parseFile(p.toFile());
            }else{
                throw new IllegalArgumentException("Invalid File Path "+p.toString());
            }

        }
    }

    /**
     *
     * @return String server hostname
     */
    public String getServerHost(){
        return config.getString("server.host");
    }

    /**
     *
     * @return int server port
     */
    public int getServerPort(){
        return config.getInt("server.port");
    }

    /**
     *
     * @return number of Client can be connected
     */
    public int getNumberOfClient(){
        return config.getInt("server.noOfClient");
    }

    /**
     *
     * @return File Channel Interval
     */
    public long getFileChannelWriteInterval(){
        return config.getLong("fileChannel.writerInverval");
    }


}
