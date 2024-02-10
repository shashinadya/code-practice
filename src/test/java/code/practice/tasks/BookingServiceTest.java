package code.practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import code.practice.model.Guest;
import code.practice.model.Room;

public class BookingServiceTest {
    private BookingService bookingService;
    private Room piccadilly, cambridge, westminister, oxford, victoria, manchester;
    private Guest john, maria, sonia, siri, bob, sandra;

    @BeforeEach
    void setUp() throws Exception {
        bookingService = new BookingService();

        piccadilly = new Room("Piccadilly", "Guest Room", 3, 125.00);
        cambridge = new Room("Cambridge", "Premiere Room", 4, 175.00);
        westminister = new Room("Westminister", "Premiere Room", 4, 200.00);
        oxford = new Room("Oxford", "Suite", 5, 225.0);
        victoria = new Room("Victoria", "Suite", 5, 225.0);
        manchester = new Room("Manchester", "Suite", 5, 250.0);

        john = new Guest("John", "Doe", false);
        maria = new Guest("Maria", "Doe", true);
        sonia = new Guest("Sonia", "Doe", true);
        siri = new Guest("Siri", "Doe", true);
        bob = new Guest("Bob", "Doe", false);
        sandra = new Guest("Sandra", "Doe", false);
    }

    @Test
    void testBook() {
        assertTrue(bookingService.book(cambridge, bob));
        assertTrue(bookingService.book(oxford, maria));
        assertTrue(bookingService.book(victoria, sonia));
        assertFalse(bookingService.book(cambridge, siri));
        assertFalse(bookingService.book(cambridge, sandra));
        assertFalse(bookingService.book(oxford, john));
        assertFalse(bookingService.book(victoria, john));
    }

    @Test
    void testTotalRevenue() {
        bookingService.book(piccadilly, john);
        bookingService.book(oxford, maria);
        bookingService.book(manchester, siri);
        bookingService.book(victoria, sonia);

        assertEquals(825, bookingService.totalRevenue());
        bookingService.book(cambridge, sandra);

        assertEquals(1000, bookingService.totalRevenue());
    }
}
