package server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.ClassForTest;

public class ServerImplDefaultTest {
    Server server;

    @Before
    public void init() {
        server = new ServerImplDefault();
    }

    @Test
    public void setContainerTest() {
        server.setContainer(new ContainerSaveConditionImplDefault());
        server.addService(new ClassForTest.CalculatorImpl(), "s");
        server = new ServerImplDefault();
    }

    @Test
    public void addAndFindServiceTest() throws ServiceNotFoundException {
        server.setContainer(new ContainerSaveConditionImplDefault());
        server.addService(new ClassForTest.CalculatorImpl(), "s");
        server.findService("s");
        try {
            server.findService("se");
            Assert.fail();
        } catch (ServiceNotFoundException e) {
        }
        server = new ServerImplDefault();
    }

    @Test
    public void buildTest() throws ServiceNotFoundException {
        server = ServerImplDefault.build();
        server.addService(new ClassForTest.CalculatorImpl(), "s");
        server.findService("s");
        server = new ServerImplDefault();
    }
}
