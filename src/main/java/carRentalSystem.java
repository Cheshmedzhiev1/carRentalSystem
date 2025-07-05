import Models.Car;
import Models.Customer;
import Models.Rental;
import Services.CarService;
import Services.CustomerService;
import Services.RentalService;
import Utils.FileHandler;
import Utils.InputValidator;

import java.util.List;

public class carRentalSystem {

    private CarService carService;
    private CustomerService customerService;
    private RentalService rentalService;
    private FileHandler fileHandler;

    // app state
    private boolean isRunning;

    public carRentalSystem() {
        this.fileHandler = new FileHandler();
        this.isRunning = true;

        // loads data from CSV
        loadSystemData();

        this.carService = new CarService();
        this.customerService = new CustomerService();
        this.rentalService = new RentalService(carService, customerService);

        System.out.println("Car Rental System initialized successfully!");
    }

    // entry point
    public static void main(String[] args) {
        try {
            displayWelcomeMessage();

            carRentalSystem system = new carRentalSystem();
            system.run();

        } catch (Exception e) {
            System.err.println("Fatal error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nThank you for using Car Rental System!");
        }
    }

    public void run() {
        while (isRunning) {
            try {
                displayMainMenu();
                int choice = InputValidator.readIntInRange("Enter your choice: ", 1, 9);
                processMainMenuChoice(choice);

                if (isRunning) {
                    InputValidator.pauseForUser("\nPress Enter to continue...");
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                InputValidator.pauseForUser("Press Enter to continue...");
            }
        }
    }

    private static void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("               CAR RENTAL MANAGEMENT SYSTEM               ");
        System.out.println("=".repeat(70));
        System.out.println("Welcome to the comprehensive car rental management solution!");
        System.out.println("Features: Car Management • Customer Management • Rental Operations");
        System.out.println("          CSV Data Persistence • Search & Reporting");
        System.out.println("=".repeat(70));
    }

    private void displayMainMenu() {
        InputValidator.clearScreen();
        InputValidator.displayHeader("MAIN MENU");

        System.out.println("MAIN MENU OPTIONS:");
        System.out.println();
        System.out.println("CAR MANAGEMENT");
        System.out.println("  1. Car Operations (Add, Edit, Remove, List, Search)");
        System.out.println();
        System.out.println("CUSTOMER MANAGEMENT");
        System.out.println("  2. Customer Operations (Add, Edit, Remove, List, Search)");
        System.out.println();
        System.out.println("RENTAL OPERATIONS");
        System.out.println("  3. Rental Management (Create, Complete, View, Search)");
        System.out.println();
        System.out.println("REPORTS & ANALYTICS");
        System.out.println("  4. View Reports and Statistics");
        System.out.println();
        System.out.println("SEARCH & UTILITIES");
        System.out.println("  5. Advanced Search Operations");
        System.out.println();
        System.out.println("DATA MANAGEMENT");
        System.out.println("  6. Data Import/Export Operations");
        System.out.println();
        System.out.println("SYSTEM UTILITIES");
        System.out.println("  7. System Maintenance");
        System.out.println();
        System.out.println("HELP & INFORMATION");
        System.out.println("  8. Help and System Information");
        System.out.println();
        System.out.println("EXIT");
        System.out.println("  9. Save Data and Exit");
        System.out.println();
        System.out.println("-".repeat(60));
    }

    private void processMainMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleCarOperations();
                break;
            case 2:
                handleCustomerOperations();
                break;
            case 3:
                handleRentalOperations();
                break;
            case 4:
                handleReportsAndStatistics();
                break;
            case 5:
                handleAdvancedSearch();
                break;
            case 6:
                handleDataManagement();
                break;
            case 7:
                handleSystemMaintenance();
                break;
            case 8:
                handleHelpAndInformation();
                break;
            case 9:
                handleExit();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // car management operations
    private void handleCarOperations() {
        boolean inCarMenu = true;

        while (inCarMenu) {
            InputValidator.displaySection("CAR MANAGEMENT");
            System.out.println("1. Add New Car");
            System.out.println("2. Edit Car Information");
            System.out.println("3. Remove Car");
            System.out.println("4. List All Cars");
            System.out.println("5. List Available Cars");
            System.out.println("6. List Rented Cars");
            System.out.println("7. Search Cars");
            System.out.println("8. Car Statistics");
            System.out.println("9. Back to Main Menu");

            int choice = InputValidator.readIntInRange("Choose option: ", 1, 9);

            switch (choice) {
                case 1:
                    carService.addCarInteractive();
                    break;
                case 2:
                    String carId = InputValidator.readCarId("Enter car ID to edit: ");
                    carService.editCarInteractive(carId);
                    break;
                case 3:
                    String removeId = InputValidator.readCarId("Enter car ID to remove: ");
                    carService.removeCar(removeId);
                    break;
                case 4:
                    carService.displayAllCars();
                    break;
                case 5:
                    carService.displayAvailableCars();
                    break;
                case 6:
                    carService.displayRentedCars();
                    break;
                case 7:
                    List<Car> searchResults = carService.searchCarsInteractive();
                    carService.displayCarList(searchResults, "Search Results");
                    break;
                case 8:
                    carService.displayCarStatistics();
                    break;
                case 9:
                    inCarMenu = false;
                    break;
            }

            if (inCarMenu) {
                InputValidator.pauseForUser("Press Enter to continue...");
            }
        }
    }

    // customer management operations
    private void handleCustomerOperations() {
        boolean inCustomerMenu = true;

        while (inCustomerMenu) {
            InputValidator.displaySection("CUSTOMER MANAGEMENT");
            System.out.println("1. Add New Customer");
            System.out.println("2. Edit Customer Information");
            System.out.println("3. Remove Customer");
            System.out.println("4. List All Customers");
            System.out.println("5. Search Customers");
            System.out.println("6. Customer Statistics");
            System.out.println("7. Validate Customer Data");
            System.out.println("8. Back to Main Menu");

            int choice = InputValidator.readIntInRange("Choose option: ", 1, 8);

            switch (choice) {
                case 1:
                    customerService.addCustomerInteractive();
                    break;
                case 2:
                    String customerId = InputValidator.readCustomerId("Enter customer ID to edit: ");
                    customerService.editCustomerInteractive(customerId);
                    break;
                case 3:
                    String removeId = InputValidator.readCustomerId("Enter customer ID to remove: ");
                    customerService.removeCustomer(removeId);
                    break;
                case 4:
                    customerService.displayAllCustomers();
                    break;
                case 5:
                    List<Customer> searchResults = customerService.searchCustomersInteractive();
                    customerService.displayCustomerList(searchResults, "Search Results");
                    break;
                case 6:
                    customerService.displayCustomerStatistics();
                    break;
                case 7:
                    customerService.displayValidationResults();
                    break;
                case 8:
                    inCustomerMenu = false;
                    break;
            }

            if (inCustomerMenu) {
                InputValidator.pauseForUser("Press Enter to continue...");
            }
        }
    }

    // rental management operations
    private void handleRentalOperations() {
        boolean inRentalMenu = true;

        while (inRentalMenu) {
            InputValidator.displaySection("RENTAL MANAGEMENT");
            System.out.println("1. Create New Rental");
            System.out.println("2. Complete Rental (Return Car)");
            System.out.println("3. Cancel Rental");
            System.out.println("4. View All Rentals");
            System.out.println("5. View Active Rentals");
            System.out.println("6. View Completed Rentals");
            System.out.println("7. View Overdue Rentals");
            System.out.println("8. View Rental Details");
            System.out.println("9. Back to Main Menu");

            int choice = InputValidator.readIntInRange("Choose option: ", 1, 9);

            switch (choice) {
                case 1:
                    rentalService.createRentalInteractive();
                    break;
                case 2:
                    rentalService.completeRentalInteractive();
                    break;
                case 3:
                    String cancelId = InputValidator.readNonEmptyString("Enter rental ID to cancel: ");
                    String reason = InputValidator.readNonEmptyString("Enter cancellation reason: ");
                    rentalService.cancelRental(cancelId, reason);
                    break;
                case 4:
                    rentalService.displayAllRentals();
                    break;
                case 5:
                    rentalService.displayActiveRentals();
                    break;
                case 6:
                    rentalService.displayCompletedRentals();
                    break;
                case 7:
                    rentalService.displayOverdueRentals();
                    break;
                case 8:
                    String rentalId = InputValidator.readNonEmptyString("Enter rental ID: ");
                    Rental rental = rentalService.findRentalById(rentalId);
                    if (rental != null) {
                        rentalService.displayRentalSummary(rental);
                    } else {
                        System.out.println("Rental not found.");
                    }
                    break;
                case 9:
                    inRentalMenu = false;
                    break;
            }

            if (inRentalMenu) {
                InputValidator.pauseForUser("Press Enter to continue...");
            }
        }
    }

    // reports and statistics operator
    private void handleReportsAndStatistics() {
        InputValidator.displaySection("REPORTS & STATISTICS");
        System.out.println("1. Car Fleet Statistics");
        System.out.println("2. Customer Statistics");
        System.out.println("3. Rental Statistics");
        System.out.println("4. Financial Summary");
        System.out.println("5. System Overview");

        int choice = InputValidator.readIntInRange("Choose report: ", 1, 5);

        switch (choice) {
            case 1:
                carService.displayCarStatistics();
                break;
            case 2:
                customerService.displayCustomerStatistics();
                break;
            case 3:
                rentalService.displayRentalStatistics();
                break;
            case 4:
                displayFinancialSummary();
                break;
            case 5:
                displaySystemOverview();
                break;
        }
    }

    // advanced search operations
    private void handleAdvancedSearch() {
        InputValidator.displaySection("ADVANCED SEARCH");
        System.out.println("1. Search Cars");
        System.out.println("2. Search Customers");
        System.out.println("3. Search Rentals by Customer");
        System.out.println("4. Search Rentals by Car");
        System.out.println("5. Search Overdue Rentals");

        int choice = InputValidator.readIntInRange("Choose search type: ", 1, 5);

        switch (choice) {
            case 1:
                List<Car> carResults = carService.searchCarsInteractive();
                carService.displayCarList(carResults, "Car Search Results");
                break;
            case 2:
                List<Customer> customerResults = customerService.searchCustomersInteractive();
                customerService.displayCustomerList(customerResults, "Customer Search Results");
                break;
            case 3:
                String customerId = InputValidator.readCustomerId("Enter customer ID: ");
                List<Rental> customerRentals = rentalService.getRentalsByCustomer(customerId);
                rentalService.displayRentalList(customerRentals, "Rentals for Customer " + customerId);
                break;
            case 4:
                String carId = InputValidator.readCarId("Enter car ID: ");
                List<Rental> carRentals = rentalService.getRentalsByCar(carId);
                rentalService.displayRentalList(carRentals, "Rentals for Car " + carId);
                break;
            case 5:
                rentalService.displayOverdueRentals();
                break;
        }
    }

    // data management operations
    private void handleDataManagement() {
        InputValidator.displaySection("DATA MANAGEMENT");
        System.out.println("1. Save Data to CSV");
        System.out.println("2. Load Data from CSV");
        System.out.println("3. Create Backup");
        System.out.println("4. Restore from Backup");
        System.out.println("5. Reset All Data");
        System.out.println("6. Create Sample Data");

        int choice = InputValidator.readIntInRange("Choose option: ", 1, 6);

        switch (choice) {
            case 1:
                saveSystemData();
                break;
            case 2:
                loadSystemData();
                break;
            case 3:
                if (fileHandler.createBackup()) {
                    System.out.println("Backup created successfully.");
                } else {
                    System.out.println("Failed to create backup.");
                }
                break;
            case 4:
                boolean confirm = InputValidator.readYesNo("This will overwrite current data. Continue?");
                if (confirm && fileHandler.restoreFromBackup()) {
                    loadSystemData();
                    System.out.println("Data restored from backup.");
                }
                break;
            case 5:
                boolean confirmReset = InputValidator.readYesNo("This will delete ALL data. Are you sure?");
                if (confirmReset) {
                    resetAllData();
                }
                break;
            case 6:
                createSampleData();
                break;
        }
    }

    // system maintenance operations
    private void handleSystemMaintenance() {
        InputValidator.displaySection("SYSTEM MAINTENANCE");
        System.out.println("1. Synchronize Rental Data");
        System.out.println("2. Validate Data Integrity");
        System.out.println("3. Clean Up Completed Rentals");
        System.out.println("4. Check File System");

        int choice = InputValidator.readIntInRange("Choose option: ", 1, 4);

        switch (choice) {
            case 1:
                rentalService.synchronizeData();
                break;
            case 2:
                validateSystemData();
                break;
            case 3:
                cleanUpOldRentals();
                break;
            case 4:
                checkFileSystem();
                break;
        }
    }

    // help and information
    private void handleHelpAndInformation() {
        InputValidator.displaySection("HELP & INFORMATION");
        System.out.println("CAR RENTAL MANAGEMENT SYSTEM");
        System.out.println();
        System.out.println("FEATURES:");
        System.out.println("• Complete car fleet management");
        System.out.println("• Customer registration with Bulgarian phone support");
        System.out.println("• Rental operations with cost calculation");
        System.out.println("• CSV data persistence without external libraries");
        System.out.println("• Advanced search and reporting capabilities");
        System.out.println("• Data validation and integrity checking");
        System.out.println();
        System.out.println(" TECHNICAL SPECIFICATIONS:");
        System.out.println("• Object-Oriented Programming principles");
        System.out.println("• Service layer architecture");
        System.out.println("• Interface-based design");
        System.out.println("• Comprehensive exception handling");
        System.out.println("• Manual CSV file operations");
        System.out.println();
        System.out.println(" ID FORMATS:");
        System.out.println("• Car IDs: C001, C002, C003...");
        System.out.println("• Customer IDs: CUST001, CUST002, CUST003...");
        System.out.println("• Rental IDs: R001, R002, R003...");
        System.out.println("• Phone Format: +359 XXX XXX XXX");
        System.out.println();
        System.out.println(" DATA STORAGE:");
        System.out.println("• Location: data/rentals.csv");
        System.out.println("• Format: Single CSV file with record types");
        System.out.println("• Backup: Automatic backup before changes");
    }

    // application exit
    private void handleExit() {
        InputValidator.displaySection("EXIT APPLICATION");

        boolean saveData = InputValidator.readYesNo("Save current data before exiting?");
        if (saveData) {
            saveSystemData();
        }

        System.out.println("Exiting Car Rental System...");
        isRunning = false;
    }

    // loads system data from CSV
    private void loadSystemData() {
        try {
            FileHandler.DataContainer data = fileHandler.readAllData();

            this.carService = new CarService(data.getCars());
            this.customerService = new CustomerService(data.getCustomers());
            this.rentalService = new RentalService(data.getRentals(), carService, customerService);

            rentalService.synchronizeData();

            System.out.println("System data loaded successfully.");

        } catch (Exception e) {
            System.err.println("Error loading data: " + e.getMessage());

            this.carService = new CarService();
            this.customerService = new CustomerService();
            this.rentalService = new RentalService(carService, customerService);

            if (!fileHandler.fileExists()) {
                boolean createSample = InputValidator.readYesNo("No data file found. Create sample data?");
                if (createSample) {
                    createSampleData();
                }
            }
        }
    }

    // saves system to CSV file
    private void saveSystemData() {
        try {
            boolean success = fileHandler.writeAllData(carService.getAllCars(), customerService.getAllCustomers(), rentalService.getAllRentals());

            if (success) {
                System.out.println("System data saved successfully.");
            } else {
                System.out.println("Failed to save system data.");
            }

        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // handles sample data for test and demonstration
    private void createSampleData() {
        InputValidator.displaySection("Creating Sample Data");


        carService.addCar(new Car("C001", "Toyota", "Camry", 2023, "Sedan"));
        carService.addCar(new Car("C002", "BMW", "X5", 2024, "SUV"));
        carService.addCar(new Car("C003", "Honda", "Civic", 2022, "Sedan"));
        carService.addCar(new Car("C004", "Mercedes", "C-Class", 2023, "Sedan"));
        carService.addCar(new Car("C005", "Audi", "Q7", 2024, "SUV"));

        customerService.addCustomer(new Customer("CUST001", "Георги Петров", "georgi.petrov@email.bg", "+359 888 123 456", "BG1234567"));
        customerService.addCustomer(new Customer("CUST002", "Мария Иванова", "maria.ivanova@abv.bg", "+359 887 234 567", "BG2345678"));
        customerService.addCustomer(new Customer("CUST003", "Димитър Стоянов", "dimitar.stoyanov@gmail.com", "+359 889 345 678", "BG3456789"));
        customerService.addCustomer(new Customer("CUST004", "Елена Николова", "elena.nikolova@yahoo.com", "+359 886 456 789", "BG4567890"));

        System.out.println("Sample data created successfully!");
        System.out.println("Created: 5 cars and 4 customers");
    }

    // displays financial summary
    private void displayFinancialSummary() {
        InputValidator.displaySection("FINANCIAL SUMMARY");

        List<Rental> completedRentals = rentalService.getCompletedRentals();
        double totalRevenue = completedRentals.stream().mapToDouble(Rental::getTotalCost).sum();

        double averageRental = completedRentals.stream().mapToDouble(Rental::getTotalCost).average().orElse(0.0);

        System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
        System.out.printf("Average Rental Value: $%.2f%n", averageRental);
        System.out.println("Completed Rentals: " + completedRentals.size());
        System.out.println("Active Rentals: " + rentalService.getActiveRentals().size());

        if (!rentalService.getOverdueRentals().isEmpty()) {
            System.out.println("Overdue Rentals: " + rentalService.getOverdueRentals().size());
        }
    }

    // system overview
    private void displaySystemOverview() {
        InputValidator.displaySection("SYSTEM OVERVIEW");

        System.out.println(" FLEET STATUS:");
        System.out.println("  Total Cars: " + carService.getAllCars().size());
        System.out.println("  Available Cars: " + carService.getAvailableCars().size());
        System.out.println("  Rented Cars: " + carService.getRentedCars().size());

        System.out.println("\n CUSTOMER BASE:");
        System.out.println("  Total Customers: " + customerService.getAllCustomers().size());

        System.out.println("\n RENTAL ACTIVITY:");
        System.out.println("  Total Rentals: " + rentalService.getAllRentals().size());
        System.out.println("  Active Rentals: " + rentalService.getActiveRentals().size());
        System.out.println("  Completed Rentals: " + rentalService.getCompletedRentals().size());

        if (!rentalService.getOverdueRentals().isEmpty()) {
            System.out.println("Overdue Rentals: " + rentalService.getOverdueRentals().size());
        }

        System.out.println("\n DATA STATUS:");
        System.out.println("  Data File: " + (fileHandler.fileExists() ? "Found" : "Missing"));
        System.out.println("  Last Operation: Data " + (fileHandler.fileExists() ? "loaded" : "initialized"));
    }

    // validates the system data integrity
    private void validateSystemData() {
        InputValidator.displaySection("DATA INTEGRITY VALIDATION");

        customerService.displayValidationResults();
        rentalService.synchronizeData();

        System.out.println("Data integrity check completed.");
    }

    // this method is optional !! it clears all completed rental data
    private void cleanUpOldRentals() {
        System.out.println("📊 Rental cleanup information:");
        System.out.println("  Total rentals: " + rentalService.getAllRentals().size());
        System.out.println("  Completed rentals: " + rentalService.getCompletedRentals().size());
        System.out.println();
        System.out.println("ℹ️ Note: Rental history is preserved for record keeping.");
        System.out.println("   No automatic cleanup is performed.");
    }

    // checks file system status
    private void checkFileSystem() {
        InputValidator.displaySection("FILE SYSTEM CHECK");

        System.out.println("Data Directory: data/");
        System.out.println("Main File: " + fileHandler.getFilePath());
        System.out.println("File Exists: " + (fileHandler.fileExists() ? "Yes" : "No"));

        if (fileHandler.fileExists()) {
            System.out.println("File accessible and readable");
        } else {
            System.out.println("Data file not found - will be created on save");
        }
    }

    // resets all the system data
    private void resetAllData() {
        this.carService = new CarService();
        this.customerService = new CustomerService();
        this.rentalService = new RentalService(carService, customerService);

        System.out.println("All system data has been reset.");
    }
}