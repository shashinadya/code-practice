package tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoomServiceTestChapter3 {
    private RoomService service;
    private Room[] rooms;

    @BeforeEach
    void setUp() {
        Room cambridge = new Room("Cambridge", "Premiere Room", 4, 200.00);
        Room manchester = new Room("Manchester", "Suite", 5, 250.00);
        Room piccadilly = new Room("Piccadilly", "Guest Room", 3, 125.00);
        Room oxford = new Room("Oxford", "Suite", 5, 225.0);
        Room victoria = new Room("Victoria", "Suite", 5, 225.00);
        Room westminster = new Room("Westminster", "Premiere Room", 4, 200.00);

        rooms = new Room[]{cambridge, manchester, piccadilly, oxford, victoria, westminster};
        service = new RoomService();
        service.createRooms(rooms);
    }

    @Test
    void testApplyDiscount() {
        service.applyDiscount(.10);
        assertEquals(199.9, rooms[0].getRate());
        assertEquals(249.9, rooms[1].getRate());
        assertEquals(124.9, rooms[2].getRate());
        assertEquals(224.9, rooms[3].getRate());
        assertEquals(224.9, rooms[4].getRate());
        assertEquals(199.9, rooms[5].getRate());
    }

    @Test
    void testGetRoomsByCapacity() {
        Collection<Room> roomsWithCapacity = service.getRoomsByCapacity(4);

        assertTrue(roomsWithCapacity.containsAll(Arrays.asList(rooms[0], rooms[1], rooms[3], rooms[4], rooms[5])));
        assertFalse(roomsWithCapacity.containsAll(Arrays.asList(rooms[2])));
    }

    @Test
    void testGetRoomByRateAndType() {
        Collection<Room> roomsWithRateAndType = service.getRoomByRateAndType(200.00, "Premiere Room");
        assertTrue(roomsWithRateAndType.contains(rooms[0]));
        assertFalse(roomsWithRateAndType.contains(rooms[1]));
        assertFalse(roomsWithRateAndType.contains(rooms[2]));
        assertFalse(roomsWithRateAndType.contains(rooms[3]));
        assertFalse(roomsWithRateAndType.contains(rooms[4]));
        assertTrue(roomsWithRateAndType.contains(rooms[5]));
    }
}
