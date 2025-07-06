package service;

import exception.UserNotFoundException;
import model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static utils.Constants.CUSTOMER;
import static utils.Constants.USERS_CSV_PATH;

public class UserService {
    private Scanner scanner = new Scanner(System.in);
    private List<User> users = new ArrayList<>();

    public UserService() {
        load(); // Load users from CSV on service startup
    }

    public List<User> getAll() {
        return users;
    }

    // User Registration
    public User signUp() {
        String id = UUID.randomUUID().toString();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        // Ensure username is unique
        while (isUsernameTaken(username)) {
            System.out.print("Username already taken!\nEnter new username: ");
            username = scanner.nextLine();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Assign customer role by default
        User user = new User(id, username, password, CUSTOMER);
        users.add(user);

        System.out.println("Customer has been created.");
        return user;
    }

    // Check if username is already taken
    private boolean isUsernameTaken(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // User Login
    public User signIn() {
        User user = handleGetUser();             // Get user by username
        handlePassword(user.getPassword());      // Validate password
        return user;
    }

    // Get user by username or throw if not found
    public User getByUsername(String username) throws UserNotFoundException {
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    // Save all users to the CSV file
    public void saveAll() {
        try (FileWriter writer = new FileWriter(USERS_CSV_PATH)) {
            for (User user : users) {
                writer
                        .append(user.getId()).append(",")
                        .append(user.getUsername()).append(",")
                        .append(user.getPassword()).append(",")
                        .append(user.getRole()).append(",")
                        .append("\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save users: " + e.getMessage());
            System.exit(0);
        }
    }

    // Load users from the CSV file
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_CSV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                String id = fields[0];
                String username = fields[1];
                String password = fields[2];
                String role = fields[3];

                users.add(new User(id, username, password, role));
            }
        } catch (IOException e) {
            System.out.println("Failed to load users: " + e.getMessage());
            System.exit(0);
        }
    }

    // Helpers for Sign-In
    private User handleGetUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        User user = null;

        while (user == null) {
            try {
                user = getByUsername(username);
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
                System.out.print("Username: ");
                username = scanner.nextLine();
            }
        }
        return user;
    }

    private void handlePassword(String userPassword) {
        System.out.print("Password: ");
        String password = scanner.nextLine();

        while (!password.equals(userPassword)) {
            System.out.println("Wrong password! Try again:");
            password = scanner.nextLine();
        }
    }
}
