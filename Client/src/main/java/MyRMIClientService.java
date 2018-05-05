import client.RMIClient;
import client.Registry;
import server.ServiceNotFoundException;

import java.io.IOException;
import java.util.Scanner;

public class MyRMIClientService {
    public static void main(String[] args) throws IOException, ServiceNotFoundException {
        Registry registry = RMIClient.createRegistry(8899);
        RMIServiceCalculator service = (RMIServiceCalculator) registry.lookup("service/calculator");
        Scanner scanner = new Scanner(System.in);
        System.out.println(service.sub(scanner.nextInt(),scanner.nextInt()));
        System.out.println(service.sumAllNumber());
        service.nullifySumAllNumber();
        System.out.println(service.sum(scanner.nextInt(),scanner.nextInt()));
        System.out.println(service.sumAllNumber());
    }
}
