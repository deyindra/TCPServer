package com.newrelic.codingchallenge.config;


import org.junit.contrib.java.lang.system.RestoreSystemProperties;

public abstract class AbstractServerConfigTest extends RestoreSystemProperties {
    protected String filePath;

    protected AbstractServerConfigTest(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
    }

    @Override
    protected void after() {
        super.after();
    }
}
