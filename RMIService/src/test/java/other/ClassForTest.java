package other;

import server.Service;

public class ClassForTest {
    private ClassForTest(){}
    public static class CalculatorImpl implements Service,Calculator{
        @Override
        public int sum(int term1, int term2) {
            return term1+term2;
        }
    }
    public static interface Calculator{
        int sum(int term1, int term2);
    }
}
