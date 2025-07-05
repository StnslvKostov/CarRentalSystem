import model.Car;
import model.User;
import service.CarService;
import service.UserService;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static utils.Constants.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarService carService = new CarService();
        UserService userService = new UserService();
        List<Car> cars = carService.getAll();
        List<User> users = userService.getAll();

        displayStartMenu();

        User currentUser = logIn(scanner, userService);

        switch (currentUser.getRole()) {
            case ADMIN ->{
                handleAdminRole(scanner, carService);
            }
            case CUSTOMER ->{
                displayCustomerMenu();
                String action = scanner.nextLine();
                while(!action.equals("0")){
                    switch (action){
                        case "1" ->{
                            //TODO
                        }
                        case "2" ->{
                            //TODO
                        }
                        case "3" ->{
                            carService.search();
                        }
                    }
                    action = scanner.nextLine();
                }
            }
        }


        carService.saveAll();
        userService.saveAll();

    }

    private static void handleAdminRole(Scanner scanner, CarService carService) {
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
    }

    private static User logIn(Scanner scanner, UserService userService) {
        String action = scanner.nextLine();

        switch (action) {
            case "1" -> {
                return userService.signUp();
            }
            case "2" -> {
                return userService.signIn();
            }
            case "0" -> System.exit(0);
        }
        return null;
    }

    private static void displayStartMenu() {
        System.out.println("Welcome to CarRental.");
        System.out.println("1.Sign up");
        System.out.println("2.Sign in");
        System.out.println("0.Exit");
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
    public static void displayCustomerMenu(){
        System.out.println(LINE_SEPARATOR);
        System.out.println("Customer car menu");
        System.out.println("1.Rent");
        System.out.println("2.Return");
        System.out.println("3.Search");
        System.out.println("0.Exit");
    }
}