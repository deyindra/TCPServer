package com.newrelic.codingchallenge.config;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SuccessServerConfigTest extends AbstractServerConfigTest {
    private int expectedPortValue;

    public SuccessServerConfigTest(String filePath, int expectedPortValue) {
        super(filePath);
        this.expectedPortValue = expectedPortValue;
    }

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
                {"", 4000},
                {" ", 4000},
                {SuccessServerConfigTest.class.getResource("/sample.conf").getPath(),7070}
        });
    }

    @Test
    public void testConfig(){
        System.setProperty(ServerConfig.CONFIG_FILE_KEY, filePath);
        ServerConfig config = new ServerConfig();
        Assert.assertEquals(config.getConfig().getInt("server.port"),expectedPortValue);

   }

}
