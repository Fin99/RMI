import client.RMIClient;
import client.Registry;
import server.ServiceNotFoundException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class MyRMIClientService {
    public static void main(String[] args) throws IOException, ServiceNotFoundException {
        Registry registry = RMIClient.createRegistry(8899);
        RMIServiceCalculator service = (RMIServiceCalculator) registry.lookup("service/calculator", true);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Резульат вычитания: " + service.sub(scanner.nextInt(), scanner.nextInt()));
        System.out.println("Сумма всех использованных чисел: " + service.sumAllNumber());
        System.out.println("Резульатт сложения: " + service.sum(scanner.nextInt(), scanner.nextInt()));
        System.out.println("Сумма всех использованных чисел: " + service.sumAllNumber());
        System.out.println("Часть 2");
        RMIServiceCalculator calculator = (RMIServiceCalculator) registry.lookup("service/my/calculator", false);
        System.out.println("Резульат вычитания: " + calculator.sub(scanner.nextInt(), scanner.nextInt()));
        System.out.println("Сумма всех использованных чисел: " + calculator.sumAllNumber());
        System.out.println("Резульатт сложения: " + calculator.sum(scanner.nextInt(), scanner.nextInt()));
        System.out.println("Сумма всех использованных чисел: " + calculator.sumAllNumber());
    }
}
