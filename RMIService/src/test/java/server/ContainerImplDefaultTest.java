package server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import other.ClassForTest;

public class ContainerImplDefaultTest {
    private ContainerSaveCondition container;

    @Before
    public void init() {
        container = new ContainerSaveConditionImplDefault();
    }

    @Test
    public void addAndGetServiceTest() throws ServiceNotFoundException {
        ClassForTest.CalculatorImpl calculator = new ClassForTest.CalculatorImpl();
        container.addService(calculator, "service");
        Assert.assertEquals(calculator, container.getService("service"));
        container = new ContainerSaveConditionImplDefault();
    }

    @Test(expected = ServiceNotFoundException.class)
    public void getServiceExceptionTest() throws ServiceNotFoundException {
        container.addService(new ClassForTest.CalculatorImpl(), "service");
        container.getService("not found me");
        container = new ContainerSaveConditionImplDefault();
    }

    @Test
    public void removeServiceTest() throws ServiceNotFoundException {
        container.addService(new ClassForTest.CalculatorImpl(), "service");
        container.removeService("servi");
        container.getService("service");
        container.removeService("service");
        try{
            container.getService("service");
            Assert.fail();
        } catch (ServiceNotFoundException e){
        }
        container = new ContainerSaveConditionImplDefault();
    }

}
