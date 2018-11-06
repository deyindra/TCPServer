package com.newrelic.codingchallenge.config;

import com.newrelic.codingchallenge.rule.ExceptionLoggingRule;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class FailedServerConfigTest extends AbstractServerConfigTest {

    public FailedServerConfigTest(String filePath) {
        super(filePath);
    }

    @Rule
    public ExceptionLoggingRule exceptionLoggingRule = new ExceptionLoggingRule();
    @Rule public ExpectedException expectedException = ExpectedException.none();



    @Before
    @Override
    public void before() throws Throwable {
        super.before();
    }

    @After
    @Override
    public void after() {
        super.after();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"abc.conf"},
                {System.getProperty("user.home")},
        });
    }

    @Test
    public void testConfig(){
        System.setProperty(ServerConfig.CONFIG_FILE_KEY, filePath);
        expectedException.expect(IllegalArgumentException.class);
        new ServerConfig();
   }

}
