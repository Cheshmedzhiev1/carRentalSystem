package Services;

import Models.Car;
import Utils.InputValidator;
import interfaces.Searchable;

import java.util.*;
import java.util.stream.Collectors;

public class CarService {

    private List<Car> cars;
    private static final double DEFAULT_DAILY_RATE = 50.0;

    public CarService() {
        this.cars = new ArrayList<>();
    }

    public CarService(List<Car> cars) {
        this.cars = cars != null ? new ArrayList<>(cars) : new ArrayList<>();
    }

    // adds car to the system
    public boolean addCar(Car car) {
        if (car == null || findCarById(car.getId()) != null) {
            return false;
        }

        cars.add(car);
        System.out.println("Car was added successfully: " + car);
        return true;
    }

    // adds car to the system through console
    public boolean addCarInteractive() {
        try {
            InputValidator.displaySection("Add New Car");

            // generates next available car id
            String carId = generateNextCarId();
            System.out.println("Generated Car ID: " + carId);

            // get car details from user
            String make = InputValidator.readNonEmptyString("Enter car make (example: Toyota, BMW): ");
            String model = InputValidator.readNonEmptyString("Enter car model (example: Camry, X5): ");

            int year = InputValidator.readIntInRange("Enter car year: ",
                    1900, java.time.LocalDate.now().getYear() + 2);

            String type = InputValidator.readCarType("Enter car type: ");

            // create and add the car
            Car newCar = new Car(carId, make, model, year, type);
            return addCar(newCar);

        } catch (Exception e) {
            System.err.println("Error adding car: " + e.getMessage());
            return false;
        }
    }

    // find car by id
    public Car findCarById(String carId) {
        return cars.stream()
                .filter(car -> car.getId().equalsIgnoreCase(carId))
                .findFirst()
                .orElse(null);
    }

    // updates existing car information
    public boolean editCarInteractive(String carId) {
        Car car = findCarById(carId);
        if (car == null) {
            System.out.println("Car with ID " + carId + " not found.");
            return false;
        }

        try {
            InputValidator.displaySection("Edit Car: " + car.getId());
            System.out.println("Current details: " + car);
            System.out.println("\nPress Enter to keep current value, or enter new value:");

            // make updates
            String newMake = InputValidator.readOptionalString("Make [" + car.getMake() + "]: ");
            if (!newMake.isEmpty()) {
                car.setMake(newMake);
            }

            // model updates
            String newModel = InputValidator.readOptionalString("Model [" + car.getModel() + "]: ");
            if (!newModel.isEmpty()) {
                car.setModel(newModel);
            }

            // year updates
            String yearInput = InputValidator.readOptionalString("Year [" + car.getYear() + "]: ");
            if (!yearInput.isEmpty() && InputValidator.isValidCarYear(yearInput)) {
                car.setYear(Integer.parseInt(yearInput));
            } else if (!yearInput.isEmpty()) {
                System.out.println("Invalid year, keeping current value.");
            }

            // type updates
            String newType = InputValidator.readOptionalString("Type [" + car.getType() + "]: ");
            if (!newType.isEmpty() && InputValidator.isValidCarType(newType)) {
                car.setType(newType.substring(0, 1).toUpperCase() + newType.substring(1).toLowerCase());
            } else if (!newType.isEmpty()) {
                System.out.println("Invalid car type, keeping current value.");
            }

            // availability updates
            if (!car.isAvailable()) {
                System.out.println("Car is currently rented to: " + car.getCurrentRenter());
                boolean forceAvailable = InputValidator.readYesNo("Force car to be available (will cancel current rental)?");
                if (forceAvailable) {
                    car.returnItem();
                    System.out.println("Car marked as available. Previous rental information cleared.");
                }
            }

            System.out.println("Car updated successfully: " + car);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating car: " + e.getMessage());
            return false;
        }
    }

    // marks car as removed (does not actually deletes it)
    public boolean removeCar(String carId) {
        Car car = findCarById(carId);
        if (car == null) {
            System.out.println("Car with ID " + carId + " not found.");
            return false;
        }

        if (!car.isAvailable()) {
            System.out.println("Cannot remove car " + carId + " - it is currently rented to " + car.getCurrentRenter());
            boolean forceRemove = InputValidator.readYesNo("Force removal (will cancel current rental)?");
            if (!forceRemove) {
                return false;
            }
        }

        boolean confirmRemoval = InputValidator.readYesNo("Are you sure you want to remove " + car + "?");
        if (confirmRemoval) {
            cars.remove(car);
            System.out.println("Car removed successfully: " + car);
            return true;
        }

        return false;
    }

    // gets all the cars in the system
    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }

    // gets all the available cars in the system
    public List<Car> getAvailableCars() {
        return cars.stream()
                .filter(Car::isAvailable)
                .collect(Collectors.toList());
    }

    // gets all the rented cars in the system
    public List<Car> getRentedCars() {
        return cars.stream()
                .filter(car -> !car.isAvailable())
                .collect(Collectors.toList());
    }

    // searches a car by a various criteria
    public List<Car> searchCars(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCars();
        }

        return cars.stream()
                .filter(car -> car.matchesSearchTerm(searchTerm))
                .collect(Collectors.toList());
    }

    // search car by id
    public List<Car> searchById(String carId) {
        return cars.stream()
                .filter(car -> car.matchesId(carId))
                .collect(Collectors.toList());
    }

    // search car by model
    public List<Car> searchByModel(String model) {
        return cars.stream()
                .filter(car -> car.matchesModel(model))
                .collect(Collectors.toList());
    }

    // search car by make
    public List<Car> searchByMake(String make) {
        return cars.stream()
                .filter(car -> car.matchesMake(make))
                .collect(Collectors.toList());
    }

    // search car by its status
    public List<Car> searchByStatus(String status) {
        return cars.stream()
                .filter(car -> car.matchesStatus(status))
                .collect(Collectors.toList());
    }

    // interactive search method
    public List<Car> searchCarsInteractive() {
        InputValidator.displaySection("Search Cars");

        System.out.println("Search options:");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Make");
        System.out.println("3. Search by Model");
        System.out.println("4. Search by Status (Available/Rented)");
        System.out.println("5. General search (all fields)");

        int choice = InputValidator.readIntInRange("Choose search type (1-5): ", 1, 5);

        switch (choice) {
            case 1:
                String carId = InputValidator.readNonEmptyString("Enter car ID: ");
                return searchById(carId);
            case 2:
                String make = InputValidator.readNonEmptyString("Enter make: ");
                return searchByMake(make);
            case 3:
                String model = InputValidator.readNonEmptyString("Enter model: ");
                return searchByModel(model);
            case 4:
                String status = InputValidator.readNonEmptyString("Enter status (Available/Rented): ");
                return searchByStatus(status);
            case 5:
                String searchTerm = InputValidator.readNonEmptyString("Enter search term: ");
                return searchCars(searchTerm);
            default:
                return new ArrayList<>();
        }
    }

    // displays all the car in formatted table
    public void displayAllCars() {
        displayCarList(getAllCars(), "All Cars");
    }

    // displays the available cars in formatted table
    public void displayAvailableCars() {
        displayCarList(getAvailableCars(), "Available Cars");
    }

    // displays the rented cars in formatted table
    public void displayRentedCars() {
        displayCarList(getRentedCars(), "Rented Cars");
    }

    // displays the list of the cars in formatted table
    public void displayCarList(List<Car> carList, String title) {
        InputValidator.displaySection(title);

        if (carList.isEmpty()) {
            System.out.println("No cars found.");
            return;
        }

        // table header
        System.out.printf("%-6s %-12s %-15s %-6s %-12s %-12s %-15s%n",
                "ID", "Make", "Model", "Year", "Type", "Status", "Current Renter");
        System.out.println("-".repeat(80));

        // table data
        for (Car car : carList) {
            System.out.printf("%-6s %-12s %-15s %-6d %-12s %-12s %-15s%n",
                    car.getId(),
                    truncate(car.getMake(), 12),
                    truncate(car.getModel(), 15),
                    car.getYear(),
                    truncate(car.getType(), 12),
                    car.getStatus(),
                    truncate(car.getCurrentRenter() != null ? car.getCurrentRenter() : "-", 15));
        }

        System.out.println("-".repeat(80));
        System.out.println("Total cars: " + carList.size());
    }

    // helper method to truncate string for table display
    private String truncate(String str, int maxLength) {
        if (str == null) return "-";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }

    // generates the next available car id
    public String generateNextCarId() {
        int maxId = 0;
        for (Car car : cars) {
            try {
                String idNum = car.getId().substring(1); // Remove 'C' prefix
                int id = Integer.parseInt(idNum);
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignores invalid IDs
            }
        }
        return "C" + String.format("%03d", maxId + 1);
    }

    // get statistics about the car fleet
    public Map<String, Object> getCarStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCars", cars.size());
        stats.put("availableCars", getAvailableCars().size());
        stats.put("rentedCars", getRentedCars().size());

        // group by make
        Map<String, Long> makeStats = cars.stream()
                .collect(Collectors.groupingBy(Car::getMake, Collectors.counting()));
        stats.put("carsByMake", makeStats);

        // group by type
        Map<String, Long> typeStats = cars.stream()
                .collect(Collectors.groupingBy(Car::getType, Collectors.counting()));
        stats.put("carsByType", typeStats);

        return stats;
    }

    // displays all the statistisc
    public void displayCarStatistics() {
        Map<String, Object> stats = getCarStatistics();

        InputValidator.displaySection("Car Fleet Statistics");

        System.out.println("Total Cars: " + stats.get("totalCars"));
        System.out.println("Available Cars: " + stats.get("availableCars"));
        System.out.println("Rented Cars: " + stats.get("rentedCars"));

        System.out.println("\nCars by Make:");
        @SuppressWarnings("unchecked")
        Map<String, Long> makeStats = (Map<String, Long>) stats.get("carsByMake");
        makeStats.forEach((make, count) -> System.out.println("  " + make + ": " + count));

        System.out.println("\nCars by Type:");
        @SuppressWarnings("unchecked")
        Map<String, Long> typeStats = (Map<String, Long>) stats.get("carsByType");
        typeStats.forEach((type, count) -> System.out.println("  " + type + ": " + count));
    }
}