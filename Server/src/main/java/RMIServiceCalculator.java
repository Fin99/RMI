import java.io.Serializable;

public interface RMIServiceCalculator extends Serializable {
    int sum(int term1, int term2);

    int sub(int term1, int term2);

    int sumAllNumber();

    void nullifySumAllNumber();
}
