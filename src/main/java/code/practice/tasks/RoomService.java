package code.practice.tasks;

import code.practice.model.Room;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class RoomService {
    //Declare a Collection to store Room Inventory
    private final Collection<Room> inventory;

    //Initialize Collection and assign it to the RoomInventory
    public RoomService() {
        inventory = new LinkedHashSet<>();
    }

    //Return the Room Inventory
    public Collection<Room> getInventory() {
        return inventory;
    }

    //Add a new Room to the Room Inventory using the provided parameters
    public void createRoom(String name, String type, int capacity, double rate) {
        this.inventory.add(new Room(name, type, capacity, rate));
    }

    //Reduces the rate of each room by the provided discount
    public void applyDiscount(final double discount) {
        inventory.forEach(r -> r.setRate(r.getRate() - discount));
    }

    //Returns a new collection of rooms that meet or exceed the provided capacity
    public Collection<Room> getRoomsByCapacity(final int requiredCapacity) {
        Collection<Room> inventoriesWithRequiredCapacity = new HashSet<>();
        inventory.forEach(r -> {
            if (r.getCapacity() >= requiredCapacity) {
                inventoriesWithRequiredCapacity.add(r);
            }
        });
        return inventoriesWithRequiredCapacity;
    }

    //Returns a new collection of rooms with a rate below the provided rate and that match the provided type
    public Collection<Room> getRoomByRateAndType(final double rate, final String type) {
        Collection<Room> inventoriesWithProvidedRateAndType = new HashSet<>();
        inventory.forEach(r -> {
            if ((r.getRate() == rate) & (r.getType().equals(type))) {
                inventoriesWithProvidedRateAndType.add(r);
            }
        });
        return inventoriesWithProvidedRateAndType;
    }

    public void createRooms(Room[] rooms) {
        inventory.addAll(Arrays.asList(rooms));
    }

    //Returns a boolean that indicates if the Room Inventory contains a Room
    public boolean hasRoom(Room room) {
        return inventory.contains(room);
    }

    //Returns all Rooms as an Array of Rooms in the **order** they were Added
    public Room[] asArray() {
        return inventory.toArray(new Room[0]);
    }

    public Collection<Room> getByType(String type) {

	/*
	   Return a new Collection of Rooms where Room#type matches the provided String.
	   The original Room Inventory collection MUST NOT BE MODIFIED.
	*/
        Collection<Room> newInventory = new HashSet<>();
        inventory.forEach(r -> {
            if (r.getType().equals(type)) {
                newInventory.add(r);
            }
        });
        return newInventory;
    }
}
