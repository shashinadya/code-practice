package code.practice.tasks;

import java.util.HashMap;
import java.util.Map;

import code.practice.model.Guest;
import code.practice.model.Room;

public class BookingService {
    private Map<Room, Guest> bookings = new HashMap<>();

    public boolean book(Room room, Guest guest) {
        /*
         * 1. The provided Guest is placed in the bookings Map and
         * associated with the provided room, only if no other guest
         * is associated with the room.
         *
         * Returns a boolean that indicates if the Guest was
         * successfully placed in the room.
         */
        return bookings.putIfAbsent(room, guest) == null;
    }

    public double totalRevenue() {
        /*
         * 2. Returns a double that totals the rate of each Room booked
         * in the bookings Map.
         */
        double totalRateOfRoomsInTheBookingsMap = 0;
        for (Map.Entry<Room, Guest> entry : bookings.entrySet()) {
            Room key = entry.getKey();
            totalRateOfRoomsInTheBookingsMap = totalRateOfRoomsInTheBookingsMap + key.getRate();
        }
        return totalRateOfRoomsInTheBookingsMap;
    }

    public Map<Room, Guest> getBookings() {
        return bookings;
    }
}
