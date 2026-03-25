import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Room Allocation Process");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocateService=new RoomAllocationService();

        Reservation r1=new Reservation("Abhi", "Single");
        Reservation r2=new Reservation("Subha", "Double");
        Reservation r3=new Reservation("Vanmathi", "Suite");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        while(bookingQueue.hasPendingRequests()){
            Reservation current=bookingQueue.getNextRequest();

            allocateService.allocateRoom(current, inventory);
        }
    }
}

class Room {
    private String type;
    private int beds;
    private int size;
    private double price;

    public Room(String type, int beds, int size, double price) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }
}

class RoomInventory {

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }

    public void updateAvailability(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }
}

class Reservation{
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType){
        this.guestName=guestName;
        this.roomType=roomType;
    }

    public String getGuestName(){
        return guestName;
    }

    public String getRoomType(){
        return roomType;
    }
}

class BookingRequestQueue{
    private Queue<Reservation> requestQueue;
    public BookingRequestQueue(){
        requestQueue=new LinkedList<>();
    }

    public void addRequest(Reservation reservation){
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest(){
        return requestQueue.poll();
    }

    public boolean hasPendingRequests(){
        return !requestQueue.isEmpty();
    }
}

class RoomAllocationService{
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        allocatedRoomIds=new HashSet<>();
        assignedRoomsByType=new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory){
        String roomType=reservation.getRoomType();
        Map<String, Integer> availability = inventory.getRoomAvailability();

        if(availability.getOrDefault(roomType,0)>0){
            String roomId=generateRoomId(roomType);

            allocatedRoomIds.add(roomId);
            assignedRoomsByType.computeIfAbsent(roomType, k->new HashSet<>()).add(roomId);

            inventory.updateAvailability(roomType, availability.get(roomType)-1);

            System.out.println("Booking Confirmed for Guest: "+reservation.getGuestName()+", Room Type: "+roomType);
        }
    }

    private String generateRoomId(String roomType){
        int count =assignedRoomsByType.getOrDefault(roomType, new HashSet<>()).size()+1;

        return roomType+"-"+count;
    }
}