package service;

import exception.CarNotFoundException;
import model.Car;
import model.Rental;
import model.User;
import utils.DateHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static utils.Constants.*;

public class RentalService {
    private Scanner scanner = new Scanner(System.in);
    private List<Rental> rentals = new ArrayList<>();
    private CarService carService;

    public RentalService(CarService carService) {
        this.carService = carService;
        load(); // Load existing rentals from file
    }

    public List<Rental> getAll() {
        return rentals;
    }

    public Rental getById(String id) {
        for (Rental rental : rentals) {
            if (rental.getId().equals(id)) {
                return rental;
            }
        }
        return null;
    }

    // Handles returning a rented car by the current user
    public void returnCar(User currentUser) {
        System.out.println("Which car are you returning:");

        List<Rental> currentUserRentals = getUserRentals(currentUser);
        int carNum = Integer.parseInt(scanner.nextLine());

        while (carNum > currentUserRentals.size() || carNum <= 0) {
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }

        Rental chosenRental = currentUserRentals.get(carNum - 1);
        Rental returnRental = getById(chosenRental.getId());
        returnRental.setStatus(RETURNED);
        System.out.println("Return successful.");
    }

    // Prints and returns current user's confirmed rentals
    public List<Rental> getUserRentals(User currentUser) {
        List<Rental> currentUserRentals = rentals.stream()
                .filter(r -> r.getUserId().equals(currentUser.getId()) && r.getStatus().equals(CONFIRMED))
                .toList();

        for (int i = 0; i < currentUserRentals.size(); i++) {
            try {
                Rental rental = currentUserRentals.get(i);
                Car reservedCar = carService.getById(rental.getCarId());
                System.out.printf(
                        "%d. %s %s %s %s (%s / %s)%n",
                        i + 1, reservedCar.getMake(), reservedCar.getModel(), reservedCar.getType(),
                        reservedCar.getYear(), rental.getStartDate(), rental.getEndDate()
                );
            } catch (CarNotFoundException e) {
                System.out.println("Internal error");
                System.exit(0);
            }
        }
        return currentUserRentals;
    }

    // Filtering UI and logic
    public void printFilter() {
        LocalDate startDateRequest = getStartDateRequest();
        LocalDate endDateRequest = getEndDateRequest();
        List<Car> availableCars = search(startDateRequest, endDateRequest);

        if (availableCars.isEmpty()) {
            System.out.println("No available cars for these dates.");
            return;
        }

        for (Car car : availableCars) {
            System.out.println(car);
        }
    }

    // Rental logic
    public void rent(String userId) {
        LocalDate startDateRequest = getStartDateRequest();
        LocalDate endDateRequest = getEndDateRequest();

        List<Car> availableCars = search(startDateRequest, endDateRequest);
        if (availableCars.isEmpty()) {
            System.out.println("No available cars for these dates.");
            return;
        }

        System.out.println("Choose a number for the desired car:");
        for (int i = 0; i < availableCars.size(); i++) {
            Car car = availableCars.get(i);
            System.out.printf("%d. %s %s %s %s%n", i + 1, car.getMake(), car.getModel(), car.getType(), car.getYear());
        }

        int carNum = Integer.parseInt(scanner.nextLine());
        while (carNum > availableCars.size() || carNum <= 0) {
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }

        Car chosenCar = availableCars.get(carNum - 1);
        Rental rental = new Rental(
                UUID.randomUUID().toString(), userId, chosenCar.getId(),
                startDateRequest, endDateRequest, CONFIRMED
        );

        rentals.add(rental);

        System.out.println("Car was rented successfully.");
        System.out.println(chosenCar);
    }

    // Saves all rental data to CSV file
    public void saveAll() {
        try (FileWriter writer = new FileWriter(RENTALS_CSV_PATH)) {
            for (Rental rental : rentals) {
                writer.append(rental.getId()).append(",")
                        .append(rental.getUserId()).append(",")
                        .append(rental.getCarId()).append(",")
                        .append(rental.getStartDate().toString()).append(",")
                        .append(rental.getEndDate().toString()).append(",")
                        .append(rental.getStatus()).append(",")
                        .append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    // Search with optional filters make, year and date availability

    private List<Car> search(LocalDate startDateRequest, LocalDate endDateRequest) {
        System.out.println("Leave blank to skip filter");
        System.out.print("Make: ");
        String make = scanner.nextLine().trim();
        System.out.print("From Year: ");
        String year = scanner.nextLine().trim();

        List<Car> filteredCars = checkForAvailableCars(startDateRequest, endDateRequest);

        if (!make.isEmpty()) {
            filteredCars = filteredCars.stream()
                    .filter(car -> car.getMake().equalsIgnoreCase(make))
                    .toList();
        }

        if (!year.isEmpty()) {
            try {
                int yearInt = Integer.parseInt(year);
                filteredCars = filteredCars.stream()
                        .filter(car -> Integer.parseInt(car.getYear()) >= yearInt)
                        .toList();
            } catch (NumberFormatException e) {
                System.out.println("Invalid year format. Skipping year filter.");
            }
        }

        return filteredCars;
    }

    // Validate end date

    private LocalDate getEndDateRequest() {
        System.out.println("End date in the following format: DD-MM-YYYY");
        String endDate = scanner.nextLine();
        return DateHelper.validateDateInput(endDate);
    }
    // Validate start date

    private LocalDate getStartDateRequest() {
        System.out.println("Start and End dates are mandatory fields.");
        System.out.println("Start date in the following format: DD-MM-YYYY");
        String startDate = scanner.nextLine();
        LocalDate startDateRequest = DateHelper.validateDateInput(startDate);

        while (!startDateRequest.isAfter(LocalDate.now())) {
            System.out.println("You have entered a past date! Enter new date:");
            startDate = scanner.nextLine();
            startDateRequest = DateHelper.validateDateInput(startDate);
        }

        return startDateRequest;
    }
    // Checks if each car is available during the requested date range

    private List<Car> checkForAvailableCars(LocalDate startDateRequest, LocalDate endDateRequest) {
        List<Car> availableCars = new ArrayList<>();

        for (Car car : carService.getAll()) {
            boolean isReserved = false;

            for (Rental rental : rentals) {
                if (car.getId().equals(rental.getCarId()) && rental.getStatus().equals(CONFIRMED)) {
                    // Check for overlapping dates
                    if ((startDateRequest.isBefore(rental.getEndDate()) && endDateRequest.isAfter(rental.getStartDate()))
                            || startDateRequest.equals(rental.getStartDate())
                            || endDateRequest.equals(rental.getEndDate())) {
                        isReserved = true;
                        break;
                    }
                }
            }

            if (!isReserved) {
                availableCars.add(car);
            }
        }

        return availableCars;
    }
    // Loads rental data from CSV file
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RENTALS_CSV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                String rentalId = fields[0];
                String userId = fields[1];
                String carId = fields[2];
                String startDate = fields[3];
                String endDate = fields[4];
                String status = fields[5];

                rentals.add(new Rental(rentalId,userId, carId, LocalDate.parse(startDate), LocalDate.parse(endDate), status));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}

