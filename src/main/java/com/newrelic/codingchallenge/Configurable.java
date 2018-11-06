package com.newrelic.codingchallenge;

import com.newrelic.codingchallenge.config.ServerConfig;

/**
 * Any Concrete class which needs to be configured or control by property should implement this class
 * @see com.newrelic.codingchallenge.storage.persist.ScheduledFileWriter
 */
public abstract class Configurable {
    protected ServerConfig config;

    protected Configurable(ServerConfig config) {
        this.config = config;
    }
}
