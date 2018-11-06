package com.newrelic.codingchallenge.storage;

import com.newrelic.codingchallenge.rule.ExceptionLoggingRule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class FailureBitStorageReaderWriterTest {

    @Rule
    public ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule public ExpectedException expectedException = ExpectedException.none();



    @Test
    @Parameters({"0","-1"})
    public void failureReaderWriterCreationTest(int concurrencyLevel) {
        expectedException.expect(IllegalArgumentException.class);
        new BitStorageReaderWriter(concurrencyLevel,10);
    }

    @Test
    @Parameters({"-1"})
    public void failureReaderWriterWriteTest(int number)  {
        expectedException.expect(IllegalArgumentException.class);
        BitStorageReaderWriter writer = new BitStorageReaderWriter(4,10);
        writer.write(number);
    }

}
