package com.newrelic.codingchallenge.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ServerConfig {
    protected static final String CONFIG_FILE_KEY = "config.path";
    private Config config;

    public ServerConfig(){
        String filePath = System.getProperty(CONFIG_FILE_KEY);
        if(("").equals(filePath.trim())){
            config = ConfigFactory.load();
        }else{
            Path p = Paths.get(filePath);
            if(Files.exists(p) && Files.isRegularFile(p)){
                config = ConfigFactory.parseFile(p.toFile());
            }else{
                throw new IllegalArgumentException("Invalid File Path "+p.toString());
            }

        }
    }

    public Config getConfig() {
        return config;
    }
}
