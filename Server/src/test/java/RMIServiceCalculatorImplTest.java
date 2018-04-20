import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class RMIServiceCalculatorImplTest {
    private RMIServiceCalculator service;
    @Before
    public void init(){
        service = new RMIServiceCalculatorImpl();
    }

    @Test
    public void sumTest(){
        Assert.assertEquals(service.sum(2, 2), 4);
    }

    @Test
    public void subTest(){
        Assert.assertEquals(service.sub(2, 2), 0);
    }

    @Test
    public void sumAllNumber(){
        init();
        service.sum(0, 1);
        service.sub(2, 3);
        Assert.assertEquals(service.sumAllNumber(), 6);
    }
}
