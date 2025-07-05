package Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputValidator {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern BULGARIAN_PHONE_PATTERN = Pattern.compile(
            "^\\+359\\s?[0-9]{3}\\s?[0-9]{3}\\s?[0-9]{3}$"
    );

    private static final Pattern CAR_ID_PATTERN = Pattern.compile("^C[0-9]{3}$");
    private static final Pattern CUSTOMER_ID_PATTERN = Pattern.compile("^CUST[0-9]{3}$");
    private static final Pattern RENTAL_ID_PATTERN = Pattern.compile("^R[0-9]{3}$");

    // reads and validates non-empty string input
    public static String readNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    // reads and validates optional string input
    public static String readOptionalString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // reads and validates integer in a specific range
    public static int readIntInRange(String prompt, int min, int max) {
        int value;
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("Please enter a number between %d and %d.%n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid integer.");
            }
        }
    }

    // reads and validates a positive double  value
    public static double readPositiveDouble(String prompt) {
        double value;
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                value = Double.parseDouble(input);

                if (value > 0) {
                    return value;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    // reads and validates a date input
    public static LocalDate readDate(String prompt) {
        LocalDate date;
        while (true) {
            try {
                System.out.print(prompt + " (format: yyyy-MM-dd): ");
                String input = scanner.nextLine().trim();
                date = LocalDate.parse(input, DATE_FORMATTER);
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd (example:  2024-12-25).");
            }
        }
    }

    // reads and validates a future data input
    public static LocalDate readFutureDate(String prompt) {
        LocalDate date;
        while (true) {
            date = readDate(prompt);
            if (date.isAfter(LocalDate.now()) || date.equals(LocalDate.now())) {
                return date;
            } else {
                System.out.println("Date must be today or in the future.");
            }
        }
    }

    // reads and validates date given after the start date
    public static LocalDate readDateAfter(String prompt, LocalDate startDate) {
        LocalDate date;
        while (true) {
            date = readDate(prompt);
            if (date.isAfter(startDate)) {
                return date;
            } else {
                System.out.println("End date must be after start date (" + startDate + ").");
            }
        }
    }

    // reads and validates the email address
    public static String readEmail(String prompt) {
        String email;
        while (true) {
            email = readNonEmptyString(prompt);
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            } else {
                System.out.println("Invalid email format. Please enter a valid email address.");
            }
        }
    }

    // reads and validates a specific bulgarian phone number
    public static String readBulgarianPhone(String prompt) {
        String phone;
        while (true) {
            System.out.print(prompt + " (format: +359 XXX XXX XXX): ");
            phone = scanner.nextLine().trim();

            if (BULGARIAN_PHONE_PATTERN.matcher(phone).matches()) {
                return phone;
            } else {
                System.out.println("Invalid Bulgarian phone number format. Please use +359 XXX XXX XXX");
                System.out.println("example: +359 888 782 060");
            }
        }
    }

    // reads and validates a car id
    public static String readCarId(String prompt) {
        String carId;
        while (true) {
            carId = readNonEmptyString(prompt).toUpperCase();
            if (CAR_ID_PATTERN.matcher(carId).matches()) {
                return carId;
            } else {
                System.out.println("Invalid car ID format. Please use format C001, C002");
            }
        }
    }

    // reads and validates a customer ID
    public static String readCustomerId(String prompt) {
        String customerId;
        while (true) {
            customerId = readNonEmptyString(prompt).toUpperCase();
            if (CUSTOMER_ID_PATTERN.matcher(customerId).matches()) {
                return customerId;
            } else {
                System.out.println("Invalid customer ID format. Please use format CUST001, CUST002");
            }
        }
    }

    // reads and validates a yes or  no response
    public static boolean readYesNo(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt + " (y/n): ");
            input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' for yes or 'n' for no.");
            }
        }
    }

    // validates if a string is a valid car year ( between 1900 - current +2)
    public static boolean isValidCarYear(String yearString) {
        try {
            int year = Integer.parseInt(yearString);
            int currentYear = LocalDate.now().getYear();
            return year >= 1900 && year <= currentYear + 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // validates if a string is an actual car type
    public static boolean isValidCarType(String carType) {
        String[] validTypes = {"Sedan", "SUV", "Hatchback", "Coupe", "Convertible", "Wagon", "Pickup"};
        for (String type : validTypes) {
            if (type.equalsIgnoreCase(carType)) {
                return true;
            }
        }
        return false;
    }

    // displays available cars to the user
    public static void displayCarTypes() {
        System.out.println("Available car types:");
        System.out.println("  • Sedan");
        System.out.println("  • SUV");
        System.out.println("  • Hatchback");
        System.out.println("  • Coupe");
        System.out.println("  • Convertible");
        System.out.println("  • Wagon");
        System.out.println("  • Pickup");
    }

    // reads and validates a car type
    public static String readCarType(String prompt) {
        String carType;
        while (true) {
            displayCarTypes();
            carType = readNonEmptyString(prompt);

            if (isValidCarType(carType)) {
                // ensure proper capitalisation
                return carType.substring(0, 1).toUpperCase() + carType.substring(1).toLowerCase();
            } else {
                System.out.println("Invalid car type. Please choose from the available options.");
            }
        }
    }

    // waits for user to confirm with "enter"
    public static void pauseForUser(String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    // clears the console
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
            }
        } catch (Exception e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    // displays a formatted header
    public static void displayHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("%-30s %30s%n", title, "Car Rental System");
        System.out.println("=".repeat(60));
    }

    // section separator
    public static void displaySection(String sectionName) {
        System.out.println("\n" + "-".repeat(40));
        System.out.println(sectionName);
        System.out.println("-".repeat(40));
    }
}