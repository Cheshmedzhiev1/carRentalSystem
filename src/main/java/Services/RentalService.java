package Services;

import Models.Car;
import Models.Customer;
import Models.Rental;
import Utils.InputValidator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class RentalService {

    private List<Rental> rentals;
    private CarService carService;
    private CustomerService customerService;
    private static final double DEFAULT_DAILY_RATE = 50.0;
    private static final double LATE_FEE_MULTIPLIER = 0.5; // late fee (50% extra)


    public RentalService(CarService carService, CustomerService customerService) {
        this.rentals = new ArrayList<>();
        this.carService = carService;
        this.customerService = customerService;
    }

    public RentalService(List<Rental> rentals, CarService carService, CustomerService customerService) {
        this.rentals = rentals != null ? new ArrayList<>(rentals) : new ArrayList<>();
        this.carService = carService;
        this.customerService = customerService;
    }

    // creates a new rental
    public Rental createRental(String customerId, String carId, LocalDate startDate,
                               LocalDate endDate, double dailyRate) {

        // validate customer exists
        Customer customer = customerService.findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " not found.");
            return null;
        }

        // validate car exists and is available
        Car car = carService.findCarById(carId);
        if (car == null) {
            System.out.println("Car with ID " + carId + " not found.");
            return null;
        }

        if (!car.isAvailable()) {
            System.out.println("Car " + carId + " is not available. Currently rented to: " + car.getCurrentRenter());
            return null;
        }

        // validate dates
        if (startDate.isBefore(LocalDate.now())) {
            System.out.println("Start date cannot be in the past.");
            return null;
        }

        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            System.out.println("End date must be after start date.");
            return null;
        }

        // generates rental ID
        String rentalId = generateNextRentalId();

        // creates rental
        Rental rental = new Rental(rentalId, customerId, carId, startDate, endDate, dailyRate);

        // rent the car
        if (car.rent(customerId, startDate, endDate)) {
            rentals.add(rental);
            System.out.println("Rental created successfully: " + rental);
            return rental;
        } else {
            System.out.println("Failed to rent car.");
            return null;
        }
    }

    // creates a new rental through console input
    public boolean createRentalInteractive() {
        try {
            InputValidator.displaySection("Create New Rental");

            // shows available cars
            List<Car> availableCars = carService.getAvailableCars();
            if (availableCars.isEmpty()) {
                System.out.println("No cars available for rental.");
                return false;
            }

            System.out.println("Available cars:");
            carService.displayCarList(availableCars, "Available Cars for Rental");

            // gets car selection
            String carId = InputValidator.readCarId("Enter car ID to rent: ");
            Car selectedCar = carService.findCarById(carId);
            if (selectedCar == null || !selectedCar.isAvailable()) {
                System.out.println("Invalid car ID or car not available.");
                return false;
            }

            // shows customers or allow creation
            System.out.println("\nCustomer selection:");
            System.out.println("1. Use existing customer");
            System.out.println("2. Create new customer");

            int choice = InputValidator.readIntInRange("Choose option (1-2): ", 1, 2);

            String customerId;
            if (choice == 1) {
                // shows existing customers
                customerService.displayAllCustomers();
                customerId = InputValidator.readCustomerId("Enter customer ID: ");

                if (customerService.findCustomerById(customerId) == null) {
                    System.out.println("Customer not found.");
                    return false;
                }
            } else {
                // creates new customer
                if (!customerService.addCustomerInteractive()) {
                    System.out.println("Failed to create customer.");
                    return false;
                }
                customerId = customerService.generateNextCustomerId();
                // get the actual last added customer ID
                List<Customer> customers = customerService.getAllCustomers();
                if (!customers.isEmpty()) {
                    customerId = customers.get(customers.size() - 1).getCustomerId();
                }
            }

            // get rental dates
            LocalDate startDate = InputValidator.readFutureDate("Enter rental start date");
            LocalDate endDate = InputValidator.readDateAfter("Enter rental end date", startDate);

            // sets daily rate (or use default)
            double dailyRate = DEFAULT_DAILY_RATE;
            boolean customRate = InputValidator.readYesNo("Use custom daily rate? (default: $" + DEFAULT_DAILY_RATE + ")");
            if (customRate) {
                dailyRate = InputValidator.readPositiveDouble("Enter daily rate: $");
            }

            // calculates and show cost
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            double totalCost = days * dailyRate;
            System.out.printf("Rental duration: %d days%n", days);
            System.out.printf("Total estimated cost: $%.2f%n", totalCost);

            // confirm rental
            boolean confirm = InputValidator.readYesNo("Confirm rental creation?");
            if (!confirm) {
                System.out.println("Rental cancelled.");
                return false;
            }

            // create the rental
            Rental rental = createRental(customerId, carId, startDate, endDate, dailyRate);
            return rental != null;

        } catch (Exception e) {
            System.err.println("Error creating rental: " + e.getMessage());
            return false;
        }
    }

    // completes a rental
    public boolean completeRental(String rentalId, LocalDate returnDate) {
        Rental rental = findRentalById(rentalId);
        if (rental == null) {
            System.out.println("Rental with ID " + rentalId + " not found.");
            return false;
        }

        if (!rental.isActive()) {
            System.out.println("Rental " + rentalId + " is not active.");
            return false;
        }

        // return the car
        Car car = carService.findCarById(rental.getCarId());
        if (car != null && car.returnItem()) {
            // complete the rental
            if (rental.completeRental(returnDate)) {

                // show rental summary
                System.out.println("Rental completed successfully!");
                displayRentalSummary(rental);

                // check for late fees
                if (returnDate.isAfter(rental.getEndDate())) {
                    long lateDays = ChronoUnit.DAYS.between(rental.getEndDate(), returnDate);
                    System.out.printf("Car returned %d day(s) late. Late fees applied.%n", lateDays);
                }

                return true;
            }
        }

        System.out.println("Failed to complete rental.");
        return false;
    }

    // interactive method to complete the rental
    public boolean completeRentalInteractive() {
        try {
            InputValidator.displaySection("Complete Rental (Return Car)");

            // show active rentals
            List<Rental> activeRentals = getActiveRentals();
            if (activeRentals.isEmpty()) {
                System.out.println("No active rentals found.");
                return false;
            }

            System.out.println("Active rentals:");
            displayRentalList(activeRentals, "Active Rentals");

            // get rental selection
            String rentalId = InputValidator.readNonEmptyString("Enter rental ID to complete: ");

            // use today as default return date
            LocalDate today = LocalDate.now();
            System.out.println("Default return date: " + today);

            boolean useToday = InputValidator.readYesNo("Use today as return date?");
            LocalDate returnDate;
            if (useToday) {
                returnDate = today;
            } else {
                returnDate = InputValidator.readDate("Enter actual return date");
            }

            return completeRental(rentalId, returnDate);

        } catch (Exception e) {
            System.err.println("Error completing rental: " + e.getMessage());
            return false;
        }
    }

    // cancels a rental
    public boolean cancelRental(String rentalId, String reason) {
        Rental rental = findRentalById(rentalId);
        if (rental == null) {
            System.out.println("Rental with ID " + rentalId + " not found.");
            return false;
        }

        if (!rental.isActive()) {
            System.out.println("Cannot cancel rental " + rentalId + " - it is not active.");
            return false;
        }

        // return the car
        Car car = carService.findCarById(rental.getCarId());
        if (car != null) {
            car.returnItem();
        }

        // cancel the rental
        if (rental.cancelRental(reason)) {
            System.out.println("Rental cancelled: " + rental);
            return true;
        }

        return false;
    }

    // finds a rental by id
    public Rental findRentalById(String rentalId) {
        return rentals.stream()
                .filter(rental -> rental.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst()
                .orElse(null);
    }

    // gets all the rentals in the system
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    // gets all the active rentals
    public List<Rental> getActiveRentals() {
        return rentals.stream()
                .filter(Rental::isActive)
                .collect(Collectors.toList());
    }

    // gets all the completed rentals
    public List<Rental> getCompletedRentals() {
        return rentals.stream()
                .filter(rental -> "COMPLETED".equals(rental.getStatus()))
                .collect(Collectors.toList());
    }

    // gets all the overdue rentals
    public List<Rental> getOverdueRentals() {
        return rentals.stream()
                .filter(Rental::isOverdue)
                .collect(Collectors.toList());
    }

    // gets rental for a specific customer
    public List<Rental> getRentalsByCustomer(String customerId) {
        return rentals.stream()
                .filter(rental -> rental.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    // gets rentals for a specific car
    public List<Rental> getRentalsByCar(String carId) {
        return rentals.stream()
                .filter(rental -> rental.getCarId().equals(carId))
                .collect(Collectors.toList());
    }

    // displays all the rentals
    public void displayAllRentals() {
        displayRentalList(getAllRentals(), "All Rentals");
    }

    // displays all the active rentals
    public void displayActiveRentals() {
        displayRentalList(getActiveRentals(), "Active Rentals");
    }

    // displays all the completed rentals
    public void displayCompletedRentals() {
        displayRentalList(getCompletedRentals(), "Completed Rentals");
    }

    // displays all the overdue rentals
    public void displayOverdueRentals() {
        List<Rental> overdueRentals = getOverdueRentals();
        displayRentalList(overdueRentals, "Overdue Rentals");

        if (!overdueRentals.isEmpty()) {
            System.out.println("\nWARNING: There are " + overdueRentals.size() + " overdue rental(s)!");
        }
    }

    // displays a list of rentals
    public void displayRentalList(List<Rental> rentalList, String title) {
        InputValidator.displaySection(title);

        if (rentalList.isEmpty()) {
            System.out.println("No rentals found.");
            return;
        }

        // table header
        System.out.printf("%-8s %-10s %-8s %-12s %-12s %-10s %-10s %-8s%n",
                "Rental", "Customer", "Car", "Start Date", "End Date", "Cost", "Status", "Days");
        System.out.println("-".repeat(85));

        // table data
        for (Rental rental : rentalList) {
            String status = rental.getStatus();
            if (rental.isOverdue()) {
                status += " (LATE)";
            }

            System.out.printf("%-8s %-10s %-8s %-12s %-12s $%-9.2f %-10s %-8d%n",
                    rental.getRentalId(),
                    truncate(rental.getCustomerId(), 10),
                    rental.getCarId(),
                    rental.getStartDate(),
                    rental.getEndDate(),
                    rental.getTotalCost(),
                    truncate(status, 10),
                    rental.getActualDuration());
        }

        System.out.println("-".repeat(85));
        System.out.println("Total rentals: " + rentalList.size());

        // summary statistics
        double totalRevenue = rentalList.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .mapToDouble(Rental::getTotalCost)
                .sum();

        if (totalRevenue > 0) {
            System.out.printf("Total revenue from completed rentals: $%.2f%n", totalRevenue);
        }
    }

    // displays a detailed rental summary
    public void displayRentalSummary(Rental rental) {
        InputValidator.displaySection("Rental Summary");

        Customer customer = customerService.findCustomerById(rental.getCustomerId());
        Car car = carService.findCarById(rental.getCarId());

        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Customer: " + (customer != null ? customer.getName() : rental.getCustomerId()));
        System.out.println("Car: " + (car != null ? car.getMake() + " " + car.getModel() : rental.getCarId()));
        System.out.println("Rental Period: " + rental.getStartDate() + " to " + rental.getEndDate());
        System.out.println("Actual Duration: " + rental.getActualDuration() + " days");
        System.out.println("Daily Rate: $" + String.format("%.2f", rental.getDailyRate()));
        System.out.println("Total Cost: $" + String.format("%.2f", rental.getTotalCost()));
        System.out.println("Status: " + rental.getStatus());

        if (rental.getActualReturnDate() != null) {
            System.out.println("Returned: " + rental.getActualReturnDate());

            if (rental.isOverdue()) {
                System.out.println("Late Days: " + rental.getDaysOverdue());
            }
        }

        if (!rental.getNotes().isEmpty()) {
            System.out.println("Notes: " + rental.getNotes());
        }
    }

    // helper method to truncate string for table display
    private String truncate(String str, int maxLength) {
        if (str == null) return "-";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }

    // generates next available rental id
    public String generateNextRentalId() {
        int maxId = 0;
        for (Rental rental : rentals) {
            try {
                String idNum = rental.getRentalId().substring(1); // Remove 'R' prefix
                int id = Integer.parseInt(idNum);
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignore invalid IDs
            }
        }
        return "R" + String.format("%03d", maxId + 1);
    }

    // gets rental statistics
    public Map<String, Object> getRentalStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRentals", rentals.size());
        stats.put("activeRentals", getActiveRentals().size());
        stats.put("completedRentals", getCompletedRentals().size());
        stats.put("overdueRentals", getOverdueRentals().size());

        // revenue statistics
        double totalRevenue = getCompletedRentals().stream()
                .mapToDouble(Rental::getTotalCost)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        double averageRentalCost = getCompletedRentals().stream()
                .mapToDouble(Rental::getTotalCost)
                .average()
                .orElse(0.0);
        stats.put("averageRentalCost", averageRentalCost);

        // duration statistics
        double averageDuration = getCompletedRentals().stream()
                .mapToLong(Rental::getActualDuration)
                .average()
                .orElse(0.0);
        stats.put("averageRentalDuration", averageDuration);

        return stats;
    }

    // displays rental statistics
    public void displayRentalStatistics() {
        Map<String, Object> stats = getRentalStatistics();

        InputValidator.displaySection("Rental Statistics");

        System.out.println("Total Rentals: " + stats.get("totalRentals"));
        System.out.println("Active Rentals: " + stats.get("activeRentals"));
        System.out.println("Completed Rentals: " + stats.get("completedRentals"));
        System.out.println("Overdue Rentals: " + stats.get("overdueRentals"));
        System.out.printf("Total Revenue: $%.2f%n", (Double) stats.get("totalRevenue"));
        System.out.printf("Average Rental Cost: $%.2f%n", (Double) stats.get("averageRentalCost"));
        System.out.printf("Average Rental Duration: %.1f days%n", (Double) stats.get("averageRentalDuration"));
    }

    // synchronize data with our availability
    public void synchronizeData() {
        int syncCount = 0;

        for (Rental rental : getActiveRentals()) {
            Car car = carService.findCarById(rental.getCarId());
            if (car != null && car.isAvailable()) {
                car.rent(rental.getCustomerId(), rental.getStartDate(), rental.getEndDate());
                syncCount++;
            }
        }

        if (syncCount > 0) {
            System.out.println("Synchronized " + syncCount + " car(s) with rental data.");
        }
    }
}