package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DateHelper {

    public static LocalDate validateDateInput(String date) {
        Scanner scanner = new Scanner(System.in);
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

    public static boolean isYearValid(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr);
            return year <= LocalDate.now().getYear() && year >= 1950;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
