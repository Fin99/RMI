import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServiceCalculator extends Serializable{
    int sum(int term1, int term2) throws RemoteException;

    int sub(int term1, int term2) throws RemoteException;

    int sumAll() throws RemoteException;
}
