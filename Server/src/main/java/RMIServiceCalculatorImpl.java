import server.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServiceCalculatorImpl implements RMIServiceCalculator, Service {
    private int sumAllTerm;

    @Override
    public int sum(int term1, int term2) throws RemoteException{
        return term1+term2;
    }

    @Override
    public int sub(Integer term1, Integer term2) throws RemoteException{
        sumAllTerm+=term1+term2;
        return term1-term2;
    }

    @Override
    public int sumAll() throws RemoteException {
        return sumAllTerm;
    }
}
