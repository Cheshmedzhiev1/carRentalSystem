package Models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {

    private String rentalId;
    private String customerId;
    private String carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private double dailyRate;
    private double totalCost;
    private String status; // // active , completed or cancelled
    private String notes;

    public Rental(String rentalId, String customerId, String carId,
                  LocalDate startDate, LocalDate endDate, double dailyRate) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyRate = dailyRate;
        this.status = "ACTIVE";
        this.actualReturnDate = null;
        this.notes = "";
        calculateTotalCost();
    }

    public Rental(String rentalId, String customerId, String carId,
                  LocalDate startDate, LocalDate endDate, LocalDate actualReturnDate,
                  double dailyRate, double totalCost, String status, String notes) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualReturnDate = actualReturnDate;
        this.dailyRate = dailyRate;
        this.totalCost = totalCost;
        this.status = status;
        this.notes = notes != null ? notes : "";
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateTotalCost();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        calculateTotalCost();
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
        if (actualReturnDate != null) {
            calculateActualCost();
        }
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
        calculateTotalCost();
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    // planned rental duration in days
    public long getPlannedDuration() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    // actual rental duration in days
    public long getActualDuration() {
        if (actualReturnDate != null) {
            return ChronoUnit.DAYS.between(startDate, actualReturnDate);
        }
        return getPlannedDuration();
    }

    // total cost based on duration (minimum of 1 day)
    private void calculateTotalCost() {
        long days = getPlannedDuration();
        if (days < 1) days = 1;
        this.totalCost = days * dailyRate;
    }

    // calculates cost based on actual return date
    private void calculateActualCost() {
        if (actualReturnDate == null) return;

        long actualDays = getActualDuration();
        if (actualDays < 1) actualDays = 1;

        double baseCost = actualDays * dailyRate;

        // pays extra (50 %) a day if late
        if (actualReturnDate.isAfter(endDate)) {
            long lateDays = ChronoUnit.DAYS.between(endDate, actualReturnDate);
            double lateFee = lateDays * dailyRate * 0.5;
            this.totalCost = baseCost + lateFee;
        } else {
            this.totalCost = baseCost;
        }
    }

    // updates status of the car
    public boolean completeRental(LocalDate returnDate) {
        if (!"ACTIVE".equals(status)) {
            return false;
        }

        this.actualReturnDate = returnDate;
        this.status = "COMPLETED";
        calculateActualCost();
        return true;
    }

    // cancels the rental
    public boolean cancelRental(String reason) {
        if ("COMPLETED".equals(status)) {
            return false;
        }

        this.status = "CANCELLED";
        this.notes = reason != null ? reason : "Cancelled";
        return true;
    }

    // checks if car is active
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    // checks if rental is overdue
    public boolean isOverdue() {
        return isActive() && LocalDate.now().isAfter(endDate);
    }

    // checks how many days is the overdue
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(endDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return String.format("Rental ID: %s | Customer: %s | Car: %s | %s to %s | Status: %s | Cost: $%.2f%s",
                rentalId, customerId, carId, startDate, endDate, status, totalCost,
                isOverdue() ? " | OVERDUE by " + getDaysOverdue() + " days" : "");
    }

    public String toCSV() {
        return String.format("RENTAL,%s,%s,%s,%s,%s,%s,%.2f,%.2f,%s,%s",
                rentalId, customerId, carId, startDate, endDate,
                actualReturnDate != null ? actualReturnDate : "",
                dailyRate, totalCost, status, notes.replace(",", ";"));
    }

    // creates a rental object based on CSV data
    public static Rental fromCSV(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 10 && "RENTAL".equals(parts[0])) {
                LocalDate actualReturn = parts[6].isEmpty() ? null : LocalDate.parse(parts[6]);
                return new Rental(
                        parts[1], // rentalId
                        parts[2], // customerId
                        parts[3], // carId
                        LocalDate.parse(parts[4]), // startDate
                        LocalDate.parse(parts[5]), // endDate
                        actualReturn, // actualReturnDate
                        Double.parseDouble(parts[7]), // dailyRate
                        Double.parseDouble(parts[8]), // totalCost
                        parts[9], // status
                        parts.length > 10 ? parts[10].replace(";", ",") : ""
                );
            }
        } catch (Exception e) {
            System.err.println("Error parsing rental from CSV: " + e.getMessage());
        }
        return null;
    }

    // generates a unique rental id
    public static String generateRentalId(java.util.List<Rental> existingRentals) {
        int maxId = 0;
        for (Rental rental : existingRentals) {
            try {
                String idNum = rental.getRentalId().replaceAll("[^0-9]", "");
                if (!idNum.isEmpty()) {
                    int id = Integer.parseInt(idNum);
                    maxId = Math.max(maxId, id);
                }
            } catch (NumberFormatException e) {
                // ignore invalid id's
            }
        }
        return "R" + String.format("%03d", maxId + 1);
    }
}