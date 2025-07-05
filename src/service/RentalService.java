package service;

import exception.CarNotFoundException;
import model.Car;
import model.Rental;
import model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static utils.Constants.RENTALS_CSV_PATH;

public class RentalService {
    private Scanner scanner = new Scanner(System.in);
    private List<Rental> rentals = new ArrayList<>();
    private CarService carService;

    public RentalService(CarService carService) {
        this.carService = carService;
        load();
    }

    public List<Rental> getAll() {
        return rentals;
    }

    public void rent(String userId){
        System.out.println("Start date in the following format: DD-MM-YYYY");
        String startDate = scanner.nextLine();
        LocalDate startDateRequest = validateDateInput(startDate);

        System.out.println("End date in the following format: DD-MM-YYYY");
        String endDate = scanner.nextLine();
        LocalDate endDateRequest = validateDateInput(endDate);

        System.out.print("Car ID: ");
        String carId = scanner.nextLine();
        Car car = carService.handleGetCar(carId);

        Rental rental = new Rental(userId, carId, startDateRequest, endDateRequest);
        System.out.println("Car was rented successfully.");
        System.out.println(car);

        rentals.add(rental);
    }

    private LocalDate validateDateInput(String date) {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate dateRequest = null;
        boolean isDateValid = false;
        while (!isDateValid) {
            try {
                dateRequest = LocalDate.parse(date, FORMATTER);
                isDateValid = true;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please try again: ");
                date = scanner.nextLine();
            }
        }
        return dateRequest;
    }

    public void saveAll() {
        try (FileWriter writer = new FileWriter(RENTALS_CSV_PATH)) {

            for (Rental rental : rentals) {
                writer
                        .append(rental.getUserId()).append(",")
                        .append(rental.getCarId()).append(",")
                        .append(rental.getStartDate().toString()).append(",")
                        .append(rental.getEndDate().toString()).append(",")
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(RENTALS_CSV_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String userId = fields[0];
                String carId = fields[1];
                String startDate = fields[2];
                String endDate = fields[3];

                rentals.add(new Rental(userId, carId, LocalDate.parse(startDate), LocalDate.parse(endDate)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
