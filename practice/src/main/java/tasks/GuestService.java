package tasks;

import model.Guest;
import model.Room;

import java.util.ArrayList;
import java.util.List;

public class GuestService {
    private List<Guest> checkinList = new ArrayList<>(100);

    public static List<Guest> filterByFavoriteRoom(List<Guest> guests, Room room) {
        /*
         *  1. Returns a new collection that contains guests from the provided collection
         *  who have indicated the provided room as the first preference in their preferred
         *  room list.
         */
        List<Guest> guestsWithProvidedRoomInPreferredRoomListLocatedInFirstPlace = new ArrayList<>();
        guests.forEach(g -> {
            if (!g.getPreferredRooms().isEmpty() && (g.getPreferredRooms().get(0).equals(room))) {
                guestsWithProvidedRoomInPreferredRoomListLocatedInFirstPlace.add(g);
            }
        });
        return guestsWithProvidedRoomInPreferredRoomListLocatedInFirstPlace;
    }

    public void checkIn(Guest guest) {
        /*
         *  2. Adds a guest to the checkinList, placing members of the loyalty program
         *  ahead of those guests not in the program. Otherwise, guests are arranged in the
         *  order they were inserted.
         */
        int insertIndex = 0;
        if (!guest.isLoyaltyProgramMember() || checkinList.isEmpty()) {
            checkinList.add(guest);
        } else {
            for (var i = 0; i < checkinList.size(); i++) {
                if (!checkinList.get(i).isLoyaltyProgramMember()) {
                    insertIndex = i;
                    break;
                }
            }
            if (insertIndex == 0 && checkinList.get(insertIndex).isLoyaltyProgramMember()) {
                checkinList.add(guest);
            } else {
                for (int i = checkinList.size() - 1; i >= insertIndex; i--) {
                    checkinList.add(i + 1, checkinList.get(i));
                }
                checkinList.set(insertIndex, guest);
            }
        }
    }

    public void swapPosition(Guest guest1, Guest guest2) {
        /*
         *  3.  Swaps the position of the two provided guests within the checkinList.
         *  If guests are not currently in the list no action is required.
         */
        int indexOfGuest1 = checkinList.indexOf(guest1);
        int indexOfGuest2 = checkinList.indexOf(guest2);
        if (indexOfGuest1 == -1 || indexOfGuest2 == -1) {
            throw new IllegalArgumentException("Guest is not found.");
        }
        checkinList.set(indexOfGuest2, guest1);
        checkinList.set(indexOfGuest1, guest2);
    }

    public List<Guest> getCheckInList() {
        return List.copyOf(checkinList);
    }
}
