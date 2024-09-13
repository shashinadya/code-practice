package practice.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import practice.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomServiceTestChapter2 {
    private RoomService service;

    private Room cambridge = new Room("Cambridge", "Premiere Room", 4, 175.00);
    private Room manchester = new Room("Manchester", "Suite", 5, 250.00);
    private Room piccadilly = new Room("Piccadilly", "Guest Room", 3, 125.00);
    private Room oxford = new Room("Oxford", "Suite", 5, 225.0);
    private Room victoria = new Room("Victoria", "Suite", 5, 225.00);
    private Room westminster = new Room("Westminster", "Premiere Room", 4, 200.00);

    @BeforeEach
    void setUp() {
        service = new RoomService();

        service.createRoom("Piccadilly", "Guest Room", 3, 125.00);
        service.createRoom("Cambridge", "Premiere Room", 4, 175.00);
        service.createRoom("Victoria", "Suite", 5, 225.00);
        service.createRoom("Westminster", "Premiere Room", 4, 200.00);
    }

    @Test
    void testHasRoom() {
        assertFalse(service.hasRoom(manchester));
        assertTrue(service.hasRoom(cambridge));
    }

    @Test
    void testAsArray() {
        Room[] rooms = service.asArray();

        assertEquals(4, rooms.length);
        assertEquals(piccadilly, rooms[0]);
        assertEquals(cambridge, rooms[1]);
        assertEquals(victoria, rooms[2]);
        assertEquals(westminster, rooms[3]);
    }

    @Test
    void testGetByType() {
        Collection<Room> guestRooms = service.getByType("Premiere Room");

        assertEquals(2, guestRooms.size());
        assertTrue(guestRooms.stream()
                .allMatch(r -> r.getType().equals("Premiere Room")));
        assertEquals(4, service.getInventory().size());
    }
}
