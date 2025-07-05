package service;

import exception.CarNotFoundException;
import model.Car;

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
        load();
    }

    public List<Car> getAll() {
        return cars;
    }

    public Car getById(String id) throws CarNotFoundException {
        for (Car car : cars) {
            if (id.equals(car.getId())) {
                return car;
            }
        }
        throw new CarNotFoundException();
    }

    public void add() {
        String id = UUID.randomUUID().toString();
        String availability = "Available";

        System.out.println("Make:");
        String make = scanner.nextLine();

        System.out.println("Model:");
        String model = scanner.nextLine();

        System.out.println("Year:");
        String year = scanner.nextLine();

        System.out.println("Type:");
        String type = scanner.nextLine();

        Car newCar = new Car(id, make, model, year, type, availability);
        System.out.printf("Car with ID: %s has been created.\n", id);
        cars.add(newCar);
    }

    public void saveAll() {
        try (FileWriter writer = new FileWriter(CARS_CSV_PATH)) {

            for (Car car : cars) {
                writer
                        .append(car.getId()).append(",")
                        .append(car.getMake()).append(",")
                        .append(car.getModel()).append(",")
                        .append(car.getYear()).append(",")
                        .append(car.getType()).append(",")
                        .append(car.getAvailability()).append(",")
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void remove() {
        System.out.println("Insert ID of car to be removed:");
        String id = scanner.nextLine();
        Car car = handleGetCar(id);
        cars.remove(car);
        System.out.println("Car has been removed.");

    }

    public void showAll(){
        for(Car car : cars){
            System.out.println(car);
        }
    }

    public void edit() {
        System.out.println("Insert ID of car to be edited:");
        String id = scanner.nextLine();
        Car car = handleGetCar(id);

        displayEditMenu();

        String action = scanner.nextLine();
        while (!action.equals("0")) {
            switch (action) {
                case "1" -> {
                    System.out.println("Current make: " + car.getMake());
                    System.out.println("New make:");
                    String newMake = scanner.nextLine();
                    car.setMake(newMake);
                }
                case "2" -> {
                    System.out.println("Current model: " + car.getModel());
                    System.out.println("New model:");
                    String newModel = scanner.nextLine();
                    car.setModel(newModel);
                }
                case "3" -> {
                    System.out.println("Current year: " + car.getYear());
                    System.out.println("New year:");
                    String newYear = scanner.nextLine();
                    car.setYear(newYear);
                }
                case "4" -> {
                    System.out.println("Current type: " + car.getType());
                    System.out.println("New type:");
                    String newType = scanner.nextLine();
                    car.setType(newType);
                }
                case "5" -> {
                    System.out.println("Current availability: " + car.getAvailability());
                    System.out.println("New availability:");
                    String newAvailability = scanner.nextLine();
                    car.setAvailability(newAvailability);
                }

            }
            System.out.println("Car has been edited.");
            displayEditMenu();
            action = scanner.nextLine();
        }

    }

    private Car handleGetCar(String id) {
        Car car = null;
        while (car == null) {
            try {
                car = getById(id);
            } catch (CarNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.println("Insert new ID:");
                id = scanner.nextLine();
            }
        }
        return car;
    }


    private void displayEditMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println("Choose which property to edit:");
        System.out.println("1.Make");
        System.out.println("2.Model");
        System.out.println("3.Year");
        System.out.println("4.Type");
        System.out.println("5.Availability");
        System.out.println("0.Back to main menu");
    }


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
                String availability = fields[5];
                cars.add(new Car(id, make, model, year, type, availability));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
