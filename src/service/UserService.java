package service;

import exception.CarNotFoundException;
import exception.UserNotFoundException;
import model.Car;
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
        load();
    }

    public List<User> getAll() {
        return users;
    }

    public User signUp() {
        String id = UUID.randomUUID().toString();

        System.out.print("Username:");
        String username = scanner.nextLine();

        System.out.print("Password:");
        String password = scanner.nextLine();

        User user = new User(id, username, password, CUSTOMER);
        System.out.printf("Customer profile with ID: %s has been created.\n", id);
        users.add(user);
        return user;
    }

    public User signIn() {

        User user = handleGetUser();
        handlePassword(user.getPassword());

        return user;
    }


    public User getByUsername(String username) throws UserNotFoundException {
        for (User user : users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

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
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }

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
