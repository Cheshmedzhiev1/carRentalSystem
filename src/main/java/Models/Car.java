package Models;

import interfaces.Rentable;
import interfaces.Searchable;

import java.time.LocalDate;

// car/s in the rental system

public class Car implements Rentable, Searchable {

    private String id;
    private String make;
    private String model;
    private int year;
    private String type;
    private boolean available;
    private String currentRenter;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;

    public Car(String id, String make, String model, int year, String type) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.available = true;
        this.currentRenter = null;
        this.rentalStartDate = null;
        this.rentalEndDate = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean rent(String customerId, LocalDate startDate, LocalDate endDate) {
        if (!available || customerId == null || startDate == null || endDate == null) {
            return false;
        }

        if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
            return false;
        }

        this.available = false;
        this.currentRenter = customerId;
        this.rentalStartDate = startDate;
        this.rentalEndDate = endDate;
        return true;
    }

    @Override
    public boolean returnItem() {
        if (available) {
            return false;
        }

        this.available = true;
        this.currentRenter = null;
        this.rentalStartDate = null;
        this.rentalEndDate = null;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getCurrentRenter() {
        return currentRenter;
    }

    @Override
    public LocalDate getRentalStartDate() {
        return rentalStartDate;
    }

    @Override
    public LocalDate getRentalEndDate() {
        return rentalEndDate;
    }

    @Override
    public boolean matchesId(String id) {
        return this.id != null && this.id.equalsIgnoreCase(id);
    }

    @Override
    public boolean matchesModel(String model) {
        return this.model != null && this.model.toLowerCase().contains(model.toLowerCase());
    }

    @Override
    public boolean matchesStatus(String status) {
        String currentStatus = available ? "Available" : "Rented";
        return currentStatus.equalsIgnoreCase(status);
    }

    @Override
    public boolean matchesMake(String make) {
        return this.make != null && this.make.toLowerCase().contains(make.toLowerCase());
    }

    @Override
    public boolean matchesSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }

        String term = searchTerm.toLowerCase();
        return (id != null && id.toLowerCase().contains(term)) ||
                (make != null && make.toLowerCase().contains(term)) ||
                (model != null && model.toLowerCase().contains(term)) ||
                (type != null && type.toLowerCase().contains(term)) ||
                String.valueOf(year).contains(term);
    }

    // check if car is available or not
    public String getStatus() {
        return available ? "Available" : "Rented";
    }

    @Override
    public String toString() {
        return String.format("ID: %s | %s %s (%d) | Type: %s | Status: %s%s",
                id, make, model, year, type, getStatus(),
                currentRenter != null ? " | Renter: " + currentRenter : "");
    }

    // creates a csv representation for file purposes
    public String toCSV() {
        return String.format("%s,%s,%s,%d,%s,%s,%s",
                id, make, model, year, type, getStatus(),
                currentRenter != null ? currentRenter : "");
    }
}