import model.Car;
import model.User;
import service.CarService;
import service.UserService;

import java.util.List;
import java.util.Scanner;

import static utils.Constants.LINE_SEPARATOR;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarService carService = new CarService();
        UserService userService = new UserService();
        List<Car> cars = carService.getAll();
        List<User> users = userService.getAll();

        displayAdminMenu();

        String action = scanner.nextLine();
        while (!action.equals("0")) {
            switch (action) {
                case "1" -> carService.add();

                case "2" -> carService.edit();

                case "3" -> carService.remove();

                case "4" -> carService.showAll();
            }
            displayAdminMenu();
            action = scanner.nextLine();
        }

        carService.saveAll();
        userService.saveAll();

    }

    public static void displayAdminMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("Admin car menu");
        System.out.println("1.Add");
        System.out.println("2.Edit");
        System.out.println("3.Remove");
        System.out.println("4.Show all");
        System.out.println("0.Exit");
    }
}