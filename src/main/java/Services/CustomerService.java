package Services;

import Models.Customer;
import Utils.InputValidator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class CustomerService {

    private List<Customer> customers;

    public CustomerService() {
        this.customers = new ArrayList<>();
    }

    public CustomerService(List<Customer> customers) {
        this.customers = customers != null ? new ArrayList<>(customers) : new ArrayList<>();
    }

    // adds new customer to the system
    public boolean addCustomer(Customer customer) {
        if (customer == null || !customer.isValid()) {
            System.out.println("Invalid customer data.");
            return false;
        }

        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }

        if (findCustomerByEmail(customer.getEmail()) != null) {
            System.out.println("Customer with email " + customer.getEmail() + " already exists.");
            return false;
        }

        if (findCustomerByLicense(customer.getLicenseNumber()) != null) {
            System.out.println("Customer with license number " + customer.getLicenseNumber() + " already exists.");
            return false;
        }

        customers.add(customer);
        System.out.println("Customer added successfully: " + customer.getName() + " (ID: " + customer.getCustomerId() + ")");
        return true;
    }

    // adds a new customer through console input
    public boolean addCustomerInteractive() {
        try {
            InputValidator.displaySection("Add New Customer");

            // generate next available customer ID
            String customerId = generateNextCustomerId();
            System.out.println("Generated Customer ID: " + customerId);

            // get customer details from user
            String name = InputValidator.readNonEmptyString("Enter full name (Bulgarian name preferred): ");
            String email = InputValidator.readEmail("Enter email address: ");
            String phone = InputValidator.readBulgarianPhone("Enter phone number: ");
            String licenseNumber = InputValidator.readNonEmptyString("Enter driver's license number: ");

            // validates if email is unique
            if (findCustomerByEmail(email) != null) {
                System.out.println("A customer with this email already exists.");
                return false;
            }

            if (findCustomerByLicense(licenseNumber) != null) {
                System.out.println("A customer with this license number already exists.");
                return false;
            }

            // creates and add the customer
            Customer newCustomer = new Customer(customerId, name, email, phone, licenseNumber);
            return addCustomer(newCustomer);

        } catch (Exception e) {
            System.err.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    // find customer by id
    public Customer findCustomerById(String customerId) {
        return customers.stream()
                .filter(customer -> customer.getCustomerId().equalsIgnoreCase(customerId))
                .findFirst()
                .orElse(null);
    }

    // find customer by email
    public Customer findCustomerByEmail(String email) {
        return customers.stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    // find customer by license number
    public Customer findCustomerByLicense(String licenseNumber) {
        return customers.stream()
                .filter(customer -> customer.getLicenseNumber().equalsIgnoreCase(licenseNumber))
                .findFirst()
                .orElse(null);
    }

    // updates an existing customer information
    public boolean editCustomerInteractive(String customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " not found.");
            return false;
        }

        try {
            InputValidator.displaySection("Edit Customer: " + customer.getCustomerId());
            System.out.println("Current details: " + customer);
            System.out.println("\nPress Enter to keep current value, or enter new value:");

            // updates name
            String newName = InputValidator.readOptionalString("Name [" + customer.getName() + "]: ");
            if (!newName.isEmpty()) {
                customer.setName(newName);
            }

            // updates email
            String newEmail = InputValidator.readOptionalString("Email [" + customer.getEmail() + "]: ");
            if (!newEmail.isEmpty()) {
                // checks if email is valid and not already taken by another customer
                if (newEmail.contains("@")) {
                    Customer existingCustomer = findCustomerByEmail(newEmail);
                    if (existingCustomer == null || existingCustomer.getCustomerId().equals(customerId)) {
                        customer.setEmail(newEmail);
                    } else {
                        System.out.println("Email already taken by another customer, keeping current value.");
                    }
                } else {
                    System.out.println("Invalid email format, keeping current value.");
                }
            }

            // updates phone
            String newPhone = InputValidator.readOptionalString("Phone [" + customer.getPhone() + "]: ");
            if (!newPhone.isEmpty()) {
                customer.setPhone(newPhone);
            }

            // updates license number
            String newLicense = InputValidator.readOptionalString("License Number [" + customer.getLicenseNumber() + "]: ");
            if (!newLicense.isEmpty()) {
                Customer existingCustomer = findCustomerByLicense(newLicense);
                if (existingCustomer == null || existingCustomer.getCustomerId().equals(customerId)) {
                    customer.setLicenseNumber(newLicense);
                } else {
                    System.out.println("License number already taken by another customer, keeping current value.");
                }
            }

            System.out.println("Customer updated successfully: " + customer);
            return true;

        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    // removes customer from the system
    public boolean removeCustomer(String customerId) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            System.out.println("Customer with ID " + customerId + " not found.");
            return false;
        }

        // warning about removing a customer
        System.out.println("Warning: Removing a customer will affect rental history.");
        boolean confirmRemoval = InputValidator.readYesNo("Are you sure you want to remove " + customer.getName() + "?");

        if (confirmRemoval) {
            customers.remove(customer);
            System.out.println("Customer removed successfully: " + customer.getName());
            return true;
        }

        return false;
    }

    // gets all the customers in the system
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    // searches a customer by various criteria
    public List<Customer> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCustomers();
        }

        return customers.stream()
                .filter(customer -> customer.matchesSearchTerm(searchTerm))
                .collect(Collectors.toList());
    }

    // searches by name
    public List<Customer> searchByName(String name) {
        return customers.stream()
                .filter(customer -> customer.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // searches by email
    public List<Customer> searchByEmail(String email) {
        return customers.stream()
                .filter(customer -> customer.getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
    }

    // interactive search
    public List<Customer> searchCustomersInteractive() {
        InputValidator.displaySection("Search Customers");

        System.out.println("Search options:");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by Email");
        System.out.println("4. General search (all fields)");

        int choice = InputValidator.readIntInRange("Choose search type (1-4): ", 1, 4);

        switch (choice) {
            case 1:
                String customerId = InputValidator.readNonEmptyString("Enter customer ID: ");
                Customer customer = findCustomerById(customerId);
                return customer != null ? Arrays.asList(customer) : new ArrayList<>();
            case 2:
                String name = InputValidator.readNonEmptyString("Enter name: ");
                return searchByName(name);
            case 3:
                String email = InputValidator.readNonEmptyString("Enter email: ");
                return searchByEmail(email);
            case 4:
                String searchTerm = InputValidator.readNonEmptyString("Enter search term: ");
                return searchCustomers(searchTerm);
            default:
                return new ArrayList<>();
        }
    }

    // displays all the customers in a formatted table
    public void displayAllCustomers() {
        displayCustomerList(getAllCustomers(), "All Customers");
    }

    // displays the list of customer in a formatted table
    public void displayCustomerList(List<Customer> customerList, String title) {
        InputValidator.displaySection(title);

        if (customerList.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        // table header
        System.out.printf("%-10s %-20s %-25s %-18s %-12s %-12s%n",
                "ID", "Name", "Email", "Phone", "License", "Registered");
        System.out.println("-".repeat(100));

        // table data
        for (Customer customer : customerList) {
            System.out.printf("%-10s %-20s %-25s %-18s %-12s %-12s%n",
                    customer.getCustomerId(),
                    truncate(customer.getName(), 20),
                    truncate(customer.getEmail(), 25),
                    truncate(customer.getPhone(), 18),
                    truncate(customer.getLicenseNumber(), 12),
                    customer.getRegistrationDate());
        }

        System.out.println("-".repeat(100));
        System.out.println("Total customers: " + customerList.size());
    }

    // helper method to truncate string for table display
    private String truncate(String str, int maxLength) {
        if (str == null) return "-";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }

    // generates next available customer id
    public String generateNextCustomerId() {
        int maxId = 0;
        for (Customer customer : customers) {
            try {
                String idNum = customer.getCustomerId().substring(4); // Remove 'CUST' prefix
                int id = Integer.parseInt(idNum);
                maxId = Math.max(maxId, id);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignores invalid IDs
            }
        }
        return "CUST" + String.format("%03d", maxId + 1);
    }

    // gets statistics about the customer
    public Map<String, Object> getCustomerStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCustomers", customers.size());

        // registration statistics by month
        Map<String, Long> registrationsByMonth = customers.stream()
                .collect(Collectors.groupingBy(
                        customer -> customer.getRegistrationDate().getYear() + "-" +
                                String.format("%02d", customer.getRegistrationDate().getMonthValue()),
                        Collectors.counting()
                ));
        stats.put("registrationsByMonth", registrationsByMonth);

        // recent registrations (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long recentRegistrations = customers.stream()
                .filter(customer -> customer.getRegistrationDate().isAfter(thirtyDaysAgo))
                .count();
        stats.put("recentRegistrations", recentRegistrations);

        return stats;
    }

    // displays customer statistics
    public void displayCustomerStatistics() {
        Map<String, Object> stats = getCustomerStatistics();

        InputValidator.displaySection("Customer Statistics");

        System.out.println("Total Customers: " + stats.get("totalCustomers"));
        System.out.println("Recent Registrations (last 30 days): " + stats.get("recentRegistrations"));

        System.out.println("\nRegistrations by Month:");
        @SuppressWarnings("unchecked")
        Map<String, Long> monthStats = (Map<String, Long>) stats.get("registrationsByMonth");
        monthStats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByKey().reversed())
                .limit(6) // Show last 6 months
                .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue()));
    }

    // validates customer data integrity
    public List<String> validateCustomerData() {
        List<String> issues = new ArrayList<>();

        // checks for duplicate emails
        Map<String, Long> emailCounts = customers.stream()
                .collect(Collectors.groupingBy(Customer::getEmail, Collectors.counting()));
        emailCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> issues.add("Duplicate email: " + entry.getKey()));

        // checks for duplicate license numbers
        Map<String, Long> licenseCounts = customers.stream()
                .collect(Collectors.groupingBy(Customer::getLicenseNumber, Collectors.counting()));
        licenseCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> issues.add("Duplicate license: " + entry.getKey()));

        // checks for invalid customers
        customers.stream()
                .filter(customer -> !customer.isValid())
                .forEach(customer -> issues.add("Invalid customer data: " + customer.getCustomerId()));

        return issues;
    }

    // displays customer data validation results
    public void displayValidationResults() {
        List<String> issues = validateCustomerData();

        InputValidator.displaySection("Customer Data Validation");

        if (issues.isEmpty()) {
            System.out.println("All customer data is valid.");
        } else {
            System.out.println("Found " + issues.size() + " issue(s):");
            issues.forEach(issue -> System.out.println("  • " + issue));
        }
    }
}