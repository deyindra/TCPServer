package com.newrelic.codingchallenge.config;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SuccessServerConfigTest extends AbstractServerConfigTest {
    private String host;
    private int port;
    private int numClient;
    private int writeInterval;

    public SuccessServerConfigTest(String filePath, String host, int port, int numClient, int writeInterval) {
        super(filePath);
        this.host = host;
        this.port = port;
        this.numClient = numClient;
        this.writeInterval = writeInterval;
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
                {"", "localhost",4000,5,5000},
                {" ", "localhost",4000,5,5000},
                {SuccessServerConfigTest.class.getResource("/sample.conf").getPath(),"localhost",7070,6,5000}
        });
    }

    @Test
    public void testConfig(){
        System.setProperty(ServerConfig.CONFIG_FILE_KEY, filePath);
        ServerConfig config = new ServerConfig();
        Assert.assertEquals(config.getServerHost(),host);
        Assert.assertEquals(config.getServerPort(),port);
        Assert.assertEquals(config.getNumberOfClient(),numClient);
        Assert.assertEquals(config.getFileChannelWriteInterval(),writeInterval);

   }

}
