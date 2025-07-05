package interfaces;

import java.time.LocalDate;

public interface Rentable {

    // rents a car to a customer for a specific period of time
    boolean rent(String customerId, LocalDate startDate, LocalDate endDate);

    boolean returnItem();

    // checks if the car is available
    boolean isAvailable();

    // get the current renter ID
    String getCurrentRenter();

    // get the rental starting date
    LocalDate getRentalStartDate();

    // get the rental ending date
    LocalDate getRentalEndDate();
}