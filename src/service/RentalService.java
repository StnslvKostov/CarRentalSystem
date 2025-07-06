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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        load();
    }

    public List<Rental> getAll() {
        return rentals;
    }

    public Rental getById(String id){
        for(Rental rental : rentals){
            if(rental.getId().equals(id)){
                return rental;
            }
        }
        return null;
    }

    public void returnCar(User currentUser) {
        List<Rental> currentUserRentals = rentals.stream().filter(rental -> rental.getUserId().equals(currentUser.getId())).toList();
        System.out.println("Which car are you returning:");

        for (int i = 0; i < currentUserRentals.size(); i++){
            try {
                Rental rental = currentUserRentals.get(i);
                Car reservedCar = carService.getById(rental.getCarId());
                System.out.printf("%d. %s %s %s %s (%s / %s)",i+1, reservedCar.getMake(), reservedCar.getModel(), reservedCar.getType(), reservedCar.getYear(), rental.getStartDate(), rental.getEndDate());
            } catch (CarNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        int carNum = Integer.parseInt(scanner.nextLine());

        while(carNum>currentUserRentals.size() || carNum<=0){
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }
        Rental chosenRental = currentUserRentals.get(carNum-1);
        Rental returnRental = getById(chosenRental.getId());
        returnRental.setStatus(RETURNED);
        System.out.println("Return successful.");
    }

    public void printFilter() {
        LocalDate startDateRequest = getStartDateRequest();
        LocalDate endDateRequest = getEndDateRequest();

        for (Car car : search(startDateRequest, endDateRequest)) {
            System.out.println(car);
        }
    }

    public void rent(String userId) {
        LocalDate startDateRequest = getStartDateRequest();
        LocalDate endDateRequest = getEndDateRequest();

        List<Car> availableCars = search(startDateRequest, endDateRequest);
        System.out.println("Choose a number for the desired car:");
        for (int i = 0; i < availableCars.size(); i++) {
            Car currentCar = availableCars.get(i);
            System.out.printf("%d. %s %s %s %s\n", i+1, currentCar.getMake(), currentCar.getModel(), currentCar.getType(), currentCar.getYear());
        }
        int carNum = Integer.parseInt(scanner.nextLine());
        while(carNum>availableCars.size() || carNum<=0){
            System.out.print("Please choose a valid number: ");
            carNum = Integer.parseInt(scanner.nextLine());
        }


        Car chosenCar = availableCars.get(carNum-1);
        Rental rental = new Rental(UUID.randomUUID().toString(),userId, chosenCar.getId(), startDateRequest, endDateRequest, CONFIRMED);
        System.out.println("Car was rented successfully.");
        System.out.println(chosenCar);

        rentals.add(rental);
    }


    public void saveAll() {
        try (FileWriter writer = new FileWriter(RENTALS_CSV_PATH)) {

            for (Rental rental : rentals) {
                writer
                        .append(rental.getId()).append(",")
                        .append(rental.getUserId()).append(",")
                        .append(rental.getCarId()).append(",")
                        .append(rental.getStartDate().toString()).append(",")
                        .append(rental.getEndDate().toString()).append(",")
                        .append(rental.getStatus()).append(",")
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

                String rentalId = fields[0];
                String userId = fields[1];
                String carId = fields[2];
                String startDate = fields[3];
                String endDate = fields[4];
                String status = fields[5];

                rentals.add(new Rental(rentalId,userId, carId, LocalDate.parse(startDate), LocalDate.parse(endDate), status));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Car> search(LocalDate startDateRequest, LocalDate endDateRequest) {
        System.out.println("Leave blank to skip filter");
        System.out.print("Make: ");
        String make = scanner.nextLine().trim();
        System.out.print("From Year: ");
        String year = scanner.nextLine().trim();

        List<Car> filteredCars = checkForAvailableCars(startDateRequest, endDateRequest);

        if (!make.equals("")) {
            filteredCars = filteredCars.stream().filter(car -> car.getMake().equalsIgnoreCase(make)).toList();
        }
        if (!year.equals("")) {
            filteredCars = filteredCars.stream().filter(car -> Integer.parseInt(car.getYear()) >= Integer.parseInt(year)).toList();
        }
        return filteredCars;
    }

    private LocalDate getEndDateRequest() {
        System.out.println("End date in the following format: DD-MM-YYYY");
        String endDate = scanner.nextLine();
        LocalDate endDateRequest = DateHelper.validateDateInput(endDate);
        return endDateRequest;
    }

    private LocalDate getStartDateRequest() {
        System.out.println("Start and End dates are mandatory fields.");
        System.out.println("Start date in the following format: DD-MM-YYYY");
        String startDate = scanner.nextLine();
        LocalDate startDateRequest = DateHelper.validateDateInput(startDate);
        return startDateRequest;
    }

    private List<Car> checkForAvailableCars(LocalDate startDateRequest, LocalDate endDateRequest) {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : carService.getAll()) {

            boolean isReserved = false;

            for (Rental rental : rentals) {

                if (car.getId().equals(rental.getCarId()) && rental.getStatus().equals("Confirmed")) {
                    if (((startDateRequest.isAfter(rental.getStartDate()) || startDateRequest.isEqual(rental.getStartDate()))
                            && (startDateRequest.isBefore(rental.getEndDate()) || startDateRequest.isEqual(rental.getEndDate())))
                            || ((endDateRequest.isAfter(rental.getStartDate()) || endDateRequest.isEqual(rental.getStartDate()))
                            && (endDateRequest.isBefore(rental.getEndDate()) || endDateRequest.isEqual(rental.getEndDate())))
                            || ((startDateRequest.isBefore(rental.getStartDate()) || startDateRequest.isEqual(rental.getStartDate()))
                            && (endDateRequest.isAfter(rental.getEndDate()) || endDateRequest.isEqual(rental.getEndDate())))) {
                        isReserved = true;
                    }
                }
            }
            if (!isReserved) {
                availableCars.add(car);
            }
        }

        return availableCars;
    }
}
