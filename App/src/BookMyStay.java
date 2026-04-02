


import java.util.*;

class Reservation {
    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> rooms;

    public RoomInventory() {
        rooms = new HashMap<>();
        rooms.put("Single", 2);
        rooms.put("Double", 2);
        rooms.put("Suite", 1);
    }

    public boolean isAvailable(String roomType) {
        return rooms.get(roomType) > 0;
    }

    public void allocate(String roomType) {
        rooms.put(roomType, rooms.get(roomType) - 1);
    }

    public void display() {
        System.out.println("\nRemaining Inventory:");
        for (String key : rooms.keySet()) {
            System.out.println(key + ": " + rooms.get(key));
        }
    }
}

class RoomAllocationService {

    private Map<String, Integer> roomCounter = new HashMap<>();

    public String allocateRoom(Reservation r, RoomInventory inventory) {

        if (!inventory.isAvailable(r.roomType)) {
            return null;
        }

        inventory.allocate(r.roomType);

        int count = roomCounter.getOrDefault(r.roomType, 0) + 1;
        roomCounter.put(r.roomType, count);

        return r.roomType + "-" + count;
    }
}

class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService) {

        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while (true) {
            Reservation reservation;

            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) break;
                reservation = bookingQueue.getRequest();
            }

            synchronized (inventory) {
                String roomId = allocationService.allocateRoom(reservation, inventory);

                if (roomId != null) {
                    System.out.println("Booking confirmed for Guest: "
                            + reservation.guestName + ", Room ID: " + roomId);
                } else {
                    System.out.println("Booking failed for Guest: "
                            + reservation.guestName);
                }
            }
        }
    }
}


public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation\n");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vannathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        Thread t1 = new Thread(new ConcurrentBookingProcessor(
                bookingQueue, inventory, allocationService));

        Thread t2 = new Thread(new ConcurrentBookingProcessor(
                bookingQueue, inventory, allocationService));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }
        inventory.display();
    }
}

