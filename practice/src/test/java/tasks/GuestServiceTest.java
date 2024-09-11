package tasks;

import java.util.List;

import model.Guest;
import model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuestServiceTest {
    private GuestService service;

    private final Room piccadilly = new Room("Piccadilly", "Guest Room", 3, 125.00);
    private final Room cambridge = new Room("Cambridge", "Premiere Room", 4, 175.00);
    private final Room westminster = new Room("Westminster", "Premiere Room", 4, 175.00);
    private final Room oxford = new Room("Oxford", "Suite", 5, 225.0);
    private final Room victoria = new Room("Victoria", "Suite", 5, 225.0);
    private final Room manchester = new Room("Manchester", "Suite", 5, 225.0);

    private Guest john, maria, sonia, siri, bob;

    @BeforeEach
    void setUp() throws Exception {
        service = new GuestService();

        john = new Guest("John", "Doe", false);
        john.getPreferredRooms().addAll(List.of(oxford, victoria, manchester));

        maria = new Guest("Maria", "Doe", true);
        maria.getPreferredRooms().addAll(List.of(cambridge, oxford));

        sonia = new Guest("Sonia", "Doe", true);
        sonia.getPreferredRooms().add(cambridge);

        siri = new Guest("Siri", "Doe", true);

        bob = new Guest("Bob", "Doe", false);
    }

    @Test
    void testFilterByFavoriteRoom() {
        assertTrue(GuestService.filterByFavoriteRoom(List.of(john, maria, sonia, siri, bob), cambridge).containsAll(List.of(maria, sonia)));
        assertFalse(GuestService.filterByFavoriteRoom(List.of(john, maria, sonia, siri, bob), cambridge).containsAll(List.of(john, siri, sonia)));
        assertTrue(GuestService.filterByFavoriteRoom(List.of(john, maria, sonia, siri, bob), oxford).contains(john));
        assertFalse(GuestService.filterByFavoriteRoom(List.of(john, maria, sonia, siri, bob), oxford).containsAll(List.of(maria, sonia, siri, bob)));
        assertTrue(GuestService.filterByFavoriteRoom(List.of(john, maria, sonia, siri, bob), victoria).isEmpty());
    }

    @Test
    void testSwapPosition() {
        service.checkIn(bob);
        service.checkIn(maria);
        service.checkIn(sonia);
        service.checkIn(john);
        service.checkIn(siri);

        service.swapPosition(maria, john);
        service.swapPosition(siri, bob);

        List<Guest> guests = service.getCheckInList();
        assertEquals(4, guests.indexOf(maria));
        assertEquals(1, guests.indexOf(sonia));
        assertEquals(3, guests.indexOf(siri));
        assertEquals(2, guests.indexOf(bob));
        assertEquals(0, guests.indexOf(john));
    }

    @Test
    void testSwapPositionWhenGuestIsNotFound() {
        service.checkIn(bob);
        service.checkIn(maria);
        service.checkIn(sonia);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.swapPosition(bob, siri);
        });
        assertEquals("Guest is not found.", exception.getMessage());
    }

    @Test
    void testCheckIn() {
        service.checkIn(bob);
        service.checkIn(maria);
        service.checkIn(sonia);
        service.checkIn(john);
        service.checkIn(siri);

        List<Guest> guests = service.getCheckInList();
        assertEquals(0, guests.indexOf(maria));
        assertEquals(1, guests.indexOf(sonia));
        assertEquals(2, guests.indexOf(siri));
        assertEquals(3, guests.indexOf(bob));
        assertEquals(4, guests.indexOf(john));
    }

    @Test
    void testCheckInWhenAllGuestsHaveLoyaltyProgramMembership() {
        service.checkIn(maria);
        service.checkIn(sonia);
        service.checkIn(siri);

        List<Guest> guests = service.getCheckInList();
        assertEquals(0, guests.indexOf(maria));
        assertEquals(1, guests.indexOf(sonia));
        assertEquals(2, guests.indexOf(siri));
    }
}
