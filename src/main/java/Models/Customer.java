package Models;

import java.time.LocalDate;

// we represent a customer in our rental system
public class Customer {


    private String customerId;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private LocalDate registrationDate;


    public Customer(String customerId, String name, String email, String phone, String licenseNumber) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.registrationDate = LocalDate.now();
    }


    public Customer(String customerId, String name, String email, String phone,
                    String licenseNumber, LocalDate registrationDate) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.licenseNumber = licenseNumber;
        this.registrationDate = registrationDate;
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    // check if customer data is valid or not
    public boolean isValid() {
        return customerId != null && !customerId.trim().isEmpty() &&
                name != null && !name.trim().isEmpty() &&
                email != null && email.contains("@") &&
                phone != null && !phone.trim().isEmpty() &&
                licenseNumber != null && !licenseNumber.trim().isEmpty();
    }

    // check if customer matches given criteria
    public boolean matchesSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }

        String term = searchTerm.toLowerCase();
        return (customerId != null && customerId.toLowerCase().contains(term)) ||
                (name != null && name.toLowerCase().contains(term)) ||
                (email != null && email.toLowerCase().contains(term)) ||
                (phone != null && phone.toLowerCase().contains(term)) ||
                (licenseNumber != null && licenseNumber.toLowerCase().contains(term));
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Email: %s | Phone: %s | License: %s | Registered: %s",
                customerId, name, email, phone, licenseNumber, registrationDate);
    }

    // creates CSV representation for file storage
    public String toCSV() {
        return String.format("CUSTOMER,%s,%s,%s,%s,%s,%s",
                customerId, name, email, phone, licenseNumber, registrationDate);
    }


    public static Customer fromCSV(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 7 && "CUSTOMER".equals(parts[0])) {
                return new Customer(
                        parts[1], // customerId
                        parts[2], // name
                        parts[3], // email
                        parts[4], // phone
                        parts[5], // licenseNumber
                        LocalDate.parse(parts[6]) // registrationDate
                );
            }
        } catch (Exception e) {
            System.err.println("Error parsing customer from CSV: " + e.getMessage());
        }
        return null;
    }

    // generates an unique customer id
    public static String generateCustomerId(java.util.List<Customer> existingCustomers) {
        int maxId = 0;
        for (Customer customer : existingCustomers) {
            try {
                String idNum = customer.getCustomerId().replaceAll("[^0-9]", "");
                if (!idNum.isEmpty()) {
                    int id = Integer.parseInt(idNum);
                    maxId = Math.max(maxId, id);
                }
            } catch (NumberFormatException e) {
                // ignore invalid id's
            }
        }
        return "CUST" + String.format("%03d", maxId + 1);
    }

    // check if two customer are same by id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId != null && customerId.equals(customer.customerId);
    }

    // creates hash code based on customer's id
    @Override
    public int hashCode() {
        return customerId != null ? customerId.hashCode() : 0;
    }
}