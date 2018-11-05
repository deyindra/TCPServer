package com.newrelic.codingchallenge.storage;

import com.newrelic.codingchallenge.rule.ExceptionLoggingRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FailureStorageCreationTest extends AbstractBitStorageTest {
    public FailureStorageCreationTest(int length) {
        super(length);
    }

    @Rule
    public ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule public ExpectedException expectedException = ExpectedException.none();


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-1},
                {0},
                {BitStorage.MAX_STORAGE+1}
        });
    }

    @Test
    public void failedStorageCreartionTest(){
        expectedException.expect(IllegalArgumentException.class);
        new BitStorage(length);
    }

}
