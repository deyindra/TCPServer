package com.newrelic.codingchallenge.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionLoggingRule implements TestRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLoggingRule.class);
    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } catch (Exception e) {
                    LOGGER.error("Message", e);
                    throw e;
                }
            }
        };
    }
}