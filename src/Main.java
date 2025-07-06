import model.User;
import service.CarService;
import service.RentalService;
import service.UserService;

import java.util.Scanner;

import static utils.Constants.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initializing service classes
        CarService carService = new CarService();
        UserService userService = new UserService();
        RentalService rentalService = new RentalService(carService);

        displayStartMenu();

        // User login (or sign up)
        User currentUser = logIn(scanner, userService);

        // Role-based access control
        switch (currentUser.getRole()) {
            case ADMIN -> handleAdminRole(scanner, carService);
            case CUSTOMER -> handleCustomerRole(scanner, rentalService, currentUser);
        }

        // Saving data to files before exit
        carService.saveAll();
        userService.saveAll();
        rentalService.saveAll();
    }

    /**
     * Handles logic for customers - renting, returning, filtering cars, etc.
     */
    private static void handleCustomerRole(Scanner scanner, RentalService rentalService, User currentUser) {
        displayCustomerMenu();
        String action = scanner.nextLine();

        while (!action.equals("0")) {
            switch (action) {
                case "1" -> rentalService.rent(currentUser.getId()); // RentACar feature
                case "2" -> rentalService.returnCar(currentUser); // Return car feature
                case "3" -> rentalService.printFilter(); // Search/filter feature
                case "4" -> rentalService.getUserRentals(currentUser); // View own rentals
                default -> System.exit(0); // Exits on unexpected input
            }
            displayCustomerMenu(); // Re-display menu after action
            action = scanner.nextLine();
        }
    }

    /**
     * Handles admin actions - managing cars
     */
    private static void handleAdminRole(Scanner scanner, CarService carService) {
        displayAdminMenu();
        String action = scanner.nextLine();

        while (!action.equals("0")) {
            switch (action) {
                case "1" -> carService.add();    // Add new car
                case "2" -> carService.edit();   // Edit existing car
                case "3" -> carService.remove(); // Remove car
                case "4" -> carService.showAll(); // Display all cars
                default -> System.exit(0); // Exit on unknown input
            }
            displayAdminMenu();
            action = scanner.nextLine();
        }
    }

    /**
     * Handles user login or registration
     */
    private static User logIn(Scanner scanner, UserService userService) {
        String action = scanner.nextLine();

        switch (action) {
            case "1" -> { return userService.signUp(); }
            case "2" -> { return userService.signIn(); }
            default -> System.exit(0);
        }
        return null;
    }

    /**
     * Display the starting menu for login/register
     */
    private static void displayStartMenu() {
        System.out.println("Welcome to CarRental.");
        System.out.println("1.Sign up");
        System.out.println("2.Sign in");
        System.out.println("0.Exit");
    }

    /**
     * Menu for Admin users
     */
    public static void displayAdminMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("Admin car menu");
        System.out.println("1.Add");
        System.out.println("2.Edit");
        System.out.println("3.Remove");
        System.out.println("4.Show all");
        System.out.println("0.Exit");
    }

    /**
     * Menu for Customer users
     */
    public static void displayCustomerMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("Customer car menu");
        System.out.println("1.Rent");
        System.out.println("2.Return");
        System.out.println("3.Search");
        System.out.println("4.Show my reservations");
        System.out.println("0.Exit");
    }
}