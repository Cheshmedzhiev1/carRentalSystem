package Utils;

import Models.Car;
import Models.Customer;
import Models.Rental;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FileHandler {

    private static final String DATA_DIRECTORY = "data";
    private static final String CSV_FILE_NAME = "rentals.csv";
    private static final String BACKUP_SUFFIX = ".backup";
    private final String filePath;
    private final String backupPath;


    public FileHandler() {
        this.filePath = DATA_DIRECTORY + File.separator + CSV_FILE_NAME;
        this.backupPath = filePath + BACKUP_SUFFIX;
        ensureDataDirectoryExists();
    }

    // if data directory does not exist, it creates one
    private void ensureDataDirectoryExists() {
        try {
            Path dataDir = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                System.out.println("Created data directory: " + DATA_DIRECTORY);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    // creates a backup of current CSV file before making any changes
    public boolean createBackup() {
        try {
            Path originalFile = Paths.get(filePath);
            if (Files.exists(originalFile)) {
                Path backupFile = Paths.get(backupPath);
                Files.copy(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup created successfully");
                return true;
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }

    // restore data  from the backup  file
    public boolean restoreFromBackup() {
        try {
            Path backupFile = Paths.get(backupPath);
            Path originalFile = Paths.get(filePath);

            if (Files.exists(backupFile)) {
                Files.copy(backupFile, originalFile, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Data restored from backup successfully");
                return true;
            } else {
                System.err.println("No backup file found");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error restoring from backup: " + e.getMessage());
            return false;
        }
    }

    // reads all the data from the CSV file
    public DataContainer readAllData() {
        List<Car> cars = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();
        List<Rental> rentals = new ArrayList<>();

        try {
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                System.out.println("CSV file not found. Starting with empty data.");
                return new DataContainer(cars, customers, rentals);
            }

            List<String> lines = Files.readAllLines(file);
            System.out.println("Reading " + lines.size() + " lines from CSV file");

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 2) continue;

                    String recordType = parts[0].trim();

                    switch (recordType.toUpperCase()) {
                        case "CAR":
                            Car car = parseCarFromCSV(line);
                            if (car != null) {
                                cars.add(car);
                            }
                            break;

                        case "CUSTOMER":
                            Customer customer = Customer.fromCSV(line);
                            if (customer != null) {
                                customers.add(customer);
                            }
                            break;

                        case "RENTAL":
                            Rental rental = Rental.fromCSV(line);
                            if (rental != null) {
                                rentals.add(rental);
                            }
                            break;

                        default:
                            System.err.println("Unknown record " + (i + 1) + ": " + recordType);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + (i + 1) + ": " + e.getMessage());
                }
            }

            System.out.println("Loaded: " + cars.size() + " cars, " +
                    customers.size() + " customers, " +
                    rentals.size() + " rentals");

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return new DataContainer(cars, customers, rentals);
    }

    // parses a car from CSV file
    private Car parseCarFromCSV(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 6 && "CAR".equals(parts[0])) {
                Car car = new Car(
                        parts[1].trim(), // id
                        parts[2].trim(), // make
                        parts[3].trim(), // model
                        Integer.parseInt(parts[4].trim()), // year
                        parts[5].trim()  // type
                );

                // sets availability status
                if (parts.length > 6) {
                    boolean available = "Available".equalsIgnoreCase(parts[6].trim());
                    car.setAvailable(available);
                }

                // sets  current renter if available
                if (parts.length > 7 && !parts[7].trim().isEmpty()) {

                }

                return car;
            }
        } catch (Exception e) {
            System.err.println("Error parsing car from CSV: " + e.getMessage());
        }
        return null;
    }

    // writes all the data to the CSV file
    public boolean writeAllData(List<Car> cars, List<Customer> customers, List<Rental> rentals) {
        try {
            // creates backup before writing
            createBackup();

            List<String> lines = new ArrayList<>();

            // header comments
            lines.add("# Car Rental System Data File");
            lines.add("# Format: RecordType,Data1,Data2,Data3,...");
            lines.add("# Generated on: " + LocalDate.now());

            // writes cars
            for (Car car : cars) {
                lines.add(String.format("CAR,%s,%s,%s,%d,%s,%s,%s",
                        car.getId(),
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        car.getType(),
                        car.getStatus(),
                        car.getCurrentRenter() != null ? car.getCurrentRenter() : ""
                ));
            }

            // writes customers
            for (Customer customer : customers) {
                lines.add(customer.toCSV());
            }

            // writes rentals
            for (Rental rental : rentals) {
                lines.add(rental.toCSV());
            }

            // writes to file
            Path file = Paths.get(filePath);
            Files.write(file, lines, StandardCharsets.UTF_8);

            System.out.println("Data saved successfully: " + cars.size() + " cars, " +
                    customers.size() + " customers, " +
                    rentals.size() + " rentals");
            return true;

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            return false;
        }
    }

    // checks if the CSV file exists
    public boolean fileExists() {
        return Files.exists(Paths.get(filePath));
    }

    // gets the file path/ directory
    public String getFilePath() {
        return filePath;
    }

    // creates an initial CSV file with sample data if none exist
    public boolean createInitialFile() {
        if (fileExists()) {
            return true; // if the file already exists
        }

        try {
            List<String> lines = Arrays.asList(
                    "# Car Rental System - Initial Data File",
                    "# Format: RecordType,Data1,Data2,Data3,...",
                    "# Generated on: " + LocalDate.now(),
                    "CAR,C001,Toyota,Camry,2023,Sedan,Available,",
                    "CAR,C002,Honda,Civic,2022,Sedan,Available,",
                    "CAR,C003,Ford,Explorer,2024,SUV,Available,"
            );

            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);
            System.out.println("Initial CSV file created with sample data");
            return true;

        } catch (IOException e) {
            System.err.println("Error creating initial file: " + e.getMessage());
            return false;
        }
    }

    // class to hold all loaded data
    public static class DataContainer {
        private final List<Car> cars;
        private final List<Customer> customers;
        private final List<Rental> rentals;

        public DataContainer(List<Car> cars, List<Customer> customers, List<Rental> rentals) {
            this.cars = cars;
            this.customers = customers;
            this.rentals = rentals;
        }

        public List<Car> getCars() {
            return cars;
        }

        public List<Customer> getCustomers() {
            return customers;
        }

        public List<Rental> getRentals() {
            return rentals;
        }
    }
}