package service;

import exception.CarNotFoundException;
import model.Car;
import utils.DateHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static utils.Constants.CARS_CSV_PATH;
import static utils.Constants.LINE_SEPARATOR;

public class CarService {
    private Scanner scanner = new Scanner(System.in);
    private List<Car> cars = new ArrayList<>();

    public CarService() {
        load(); // Load cars from CSV on startup
    }

    public List<Car> getAll() {
        return cars;
    }

    // Find a car by ID or throw custom exception
    public Car getById(String id) throws CarNotFoundException {
        for (Car car : cars) {
            if (id.equals(car.getId())) {
                return car;
            }
        }
        throw new CarNotFoundException(); // Should include message ideally
    }

    // Add a new car
    public void add() {
        String id = UUID.randomUUID().toString();

        System.out.print("Make: ");
        String make = scanner.nextLine();

        System.out.print("Model: ");
        String model = scanner.nextLine();

        System.out.print("Year: ");
        String year = scanner.nextLine();
        while (!DateHelper.isYearValid(year)) {
            System.out.print("Please enter a valid year: ");
            year = scanner.nextLine();
        }

        System.out.print("Type: ");
        String type = scanner.nextLine();

        Car newCar = new Car(id, make, model, year, type);
        cars.add(newCar);
        System.out.println("Car has been created.");
    }

    // Save all cars to the CSV file
    public void saveAll() {
        try (FileWriter writer = new FileWriter(CARS_CSV_PATH)) {
            for (Car car : cars) {
                writer.append(car.getId()).append(",")
                        .append(car.getMake()).append(",")
                        .append(car.getModel()).append(",")
                        .append(car.getYear()).append(",")
                        .append(car.getType()).append(",")
                        .append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    // Remove a car
    public void remove() {
        System.out.println("Choose a car to be removed: ");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            System.out.printf("%d. %s %s %s %s\n", i + 1, car.getMake(), car.getModel(), car.getType(), car.getYear());
        }

        int carNum = Integer.parseInt(scanner.nextLine());
        while (carNum > cars.size() || carNum <= 0) {
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }

        cars.remove(carNum - 1);
        System.out.println("Car has been removed.");
    }

    // Displays all cars
    public void showAll() {
        for (Car car : cars) {
            System.out.println(car);
        }
    }

    // Edit car fields
    public void edit() {
        System.out.println("Choose a car to be edited: ");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            System.out.printf("%d. %s %s %s %s\n", i + 1, car.getMake(), car.getModel(), car.getType(), car.getYear());
        }

        int carNum = Integer.parseInt(scanner.nextLine());
        while (carNum > cars.size() || carNum <= 0) {
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }

        Car car = cars.get(carNum - 1);

        displayEditMenu();
        String action = scanner.nextLine();

        while (!action.equals("0")) {
            switch (action) {
                case "1" -> {
                    System.out.println("Current make: " + car.getMake());
                    System.out.print("New make: ");
                    car.setMake(scanner.nextLine());
                }
                case "2" -> {
                    System.out.println("Current model: " + car.getModel());
                    System.out.print("New model: ");
                    car.setModel(scanner.nextLine());
                }
                case "3" -> {
                    System.out.println("Current year: " + car.getYear());
                    System.out.print("New year: ");
                    String newYear = scanner.nextLine();
                    while (!DateHelper.isYearValid(newYear)) {
                        System.out.print("Please enter a valid year: ");
                        newYear = scanner.nextLine();
                    }
                    car.setYear(newYear);
                }
                case "4" -> {
                    System.out.println("Current type: " + car.getType());
                    System.out.print("New type: ");
                    car.setType(scanner.nextLine());
                }
            }
            System.out.println("Car has been edited.");
            displayEditMenu();
            action = scanner.nextLine();
        }
    }

    // Show edit options
    private void displayEditMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("Choose which property to edit:");
        System.out.println("1.Make");
        System.out.println("2.Model");
        System.out.println("3.Year");
        System.out.println("4.Type");
        System.out.println("0.Back to main menu");
    }

    // Load cars from the CSV file
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CARS_CSV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String id = fields[0];
                String make = fields[1];
                String model = fields[2];
                String year = fields[3];
                String type = fields[4];
                cars.add(new Car(id, make, model, year, type));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
