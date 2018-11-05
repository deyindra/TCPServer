package com.newrelic.codingchallenge.storage;

import com.newrelic.codingchallenge.rule.ExceptionLoggingRule;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class FailureStorageSetOrGetTest {

    @Rule
    public ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule public ExpectedException expectedException = ExpectedException.none();



    @Test
    @Parameters({"10, -1",
            "10, 10",
            "10, 11"})
    public void failureSetTest(int size, int index) {
        expectedException.expect(ArrayIndexOutOfBoundsException.class);
        BitStorage storage = new BitStorage(size);
        storage.set(index);

    }

    @Test
    @Parameters({"10, -1",
            "10, 10",
            "10, 11"})
    public void failureGetTest(int size, int index)  {
        expectedException.expect(ArrayIndexOutOfBoundsException.class);
        BitStorage storage = new BitStorage(size);
        storage.get(index);

    }

}
