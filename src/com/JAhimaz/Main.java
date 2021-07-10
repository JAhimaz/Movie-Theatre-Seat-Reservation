package com.JAhimaz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

import com.JAhimaz.ioh.InputHandling;

public class Main {

    public static void main(String[] args) {
        AdminSetup();
    }

    public static void AdminSetup(){
        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                            ║");
        System.out.println("║                     >>> Movie Seat Booking System <<<                      ║");
        System.out.println("║                         >> ADMINISTRATOR SETUP <<                          ║");
        System.out.println("║                                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
        System.out.print("\nPlease Enter The Number of SEATS in the Theatre: ");
        int numberOfSeats = InputHandling.Integer(100, 200);
        System.out.print("Number of SEATS set to: " + numberOfSeats + "\n");

        System.out.print("\nPlease Enter The Number of USERS in the Theatre: ");
        int numberOfUsers = InputHandling.Integer(1000, 2000);
        System.out.print("Number of USERS set to: " + numberOfUsers + "\n");

        bookingScenario(numberOfSeats, numberOfUsers);
    }

    public static void bookingScenario(int numberOfSeats, int numberOfUsers){
        Theatre theatre = new Theatre(numberOfSeats, "Sunway Pyramid TGV");

        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);

        for(int i = 0; i < numberOfUsers; i++){
            Runnable user = new User(theatre);
            executor.execute(user);
        }

        executor.shutdown();  
        while (!executor.isTerminated()) {   }  

        for (int i = 0; i < theatre.getNumberOfSeats(); i++) {
            System.out.println("Seat Number (" + (theatre.returnSeats().get(i).getSeatNumber() + 1) + ") : " + theatre.returnSeats().get(i).getStatusOfSeat() + " | Owned By: " + theatre.returnSeats().get(i).getPurchaser());
        }
        
        if(!theatre.isAvailableSeats()){
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\nTheatre is Fully Booked\n");
            System.out.println("════════════════════════════════════════════════════════════════════════════════\n\n");
        }else{
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\nTheatre Still Has Remaining Available Seats\n");
            System.out.println("════════════════════════════════════════════════════════════════════════════════\n\n");
        }
    }
}

class Theatre {
    
    private String theatreName;
    private int numberOfSeats;
    private List<Seat> seats = new ArrayList<Seat>();

    Theatre(int numberOfSeats, String name){
        theatreName = name;
        this.numberOfSeats = numberOfSeats;
        for (int i = 0; i < numberOfSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public synchronized List<Seat> returnSeats(){
        return seats;
    }

    public synchronized Boolean isAvailableSeats(){
        for (Seat seat : seats) {
            if(seat.getReservationStatus() == false){
                return true;
            }
        }

        return false;
    }

    public int getNumberOfSeats(){
        return numberOfSeats;
    }

    public String getTheatreName(){
        return theatreName;
    }
}

class Seat {
    private Boolean reserved = false;
    private Boolean purchased = false;
    private String purchasedBy;
    private int seatNumber;

    Seat(int seatNumber){
        this.seatNumber = seatNumber;
    }

    public int getSeatNumber(){
        return seatNumber;
    }

    public synchronized Boolean setReserved(){
        if(reserved == false){
            reserved = true;
            return true;
        }else{
            return false;
        }
    }

    public synchronized void setPurchased(){
        purchased = true;
    }

    public synchronized Boolean getReservationStatus(){
        return reserved;
    }

    public synchronized void purchasedBy(String purchaser){
        purchasedBy = purchaser;
    }

    public synchronized String getPurchaser(){
        return purchasedBy;
    }

    public synchronized String getStatusOfSeat(){
        if(reserved == true && purchased == false){ return "Reserved"; }
        if(reserved == true && purchased == true){ return "Purchased"; }
        return "Available";
    }

}

class User implements Runnable {

    // private String nameOfPerson;
    private Theatre theatre;
    private int seatsToPurchase;
    private int seatsPurchased = 0;

    private List<Integer> reservedSeats = new ArrayList<Integer>();
    

    User(Theatre theatre){
        // nameOfPerson = name;
        this.theatre = theatre;
        seatsToPurchase = (int) (Math.random() * ((3 - 1) + 1) + 1);
    }

    public synchronized void run() {

        // Loop through each seat
        for (int j = 0; j < theatre.getNumberOfSeats(); j++) {
            // if the User reaches the maximum amount of seats he wished to purchase
            if(seatsPurchased == seatsToPurchase || !theatre.isAvailableSeats()){ break; }
            // if seat is reserved continue
            if(theatre.returnSeats().get(j).getReservationStatus() == true){
                continue;
            }else{
                Boolean reserved = theatre.returnSeats().get(j).setReserved();
                if(reserved){
                    reservedSeats.add(theatre.returnSeats().get(j).getSeatNumber());
                    System.out.println(Thread.currentThread().getName() + " Reserved " + theatre.returnSeats().get(j).getSeatNumber());
                }
                int seatNumber = j;
                for(int x = 0; x < seatsToPurchase; x++){
                    if(!theatre.isAvailableSeats()){
                        break;
                    }
                    
                    if(theatre.returnSeats().get(seatNumber).getReservationStatus() == true){
                        x -= 1;
                    }else{
                        Boolean seatReserved = theatre.returnSeats().get(seatNumber).setReserved();
                        if(seatReserved){
                            reservedSeats.add(theatre.returnSeats().get(seatNumber).getSeatNumber());
                            System.out.println(Thread.currentThread().getName() + " Reserved " + theatre.returnSeats().get(seatNumber).getSeatNumber());
                        }
                    }
                    seatNumber += 1;
                }
                j = seatNumber;
                purchaseSeats();

            }
        }

        if(seatsPurchased > 0){
            System.out.println(Thread.currentThread().getName() + " purchased " + seatsPurchased + " seat(s)." + " | Reserved: " + reservedSeats.size());
            // for (Integer seat : reservedSeats) {
            //     System.out.println(seat + ",");
            // }
        }

    }


    private void purchaseSeats(){
        try {  Thread.sleep(((int) (Math.random() * ((1000 - 500) + 500) + 1)));  } catch (InterruptedException e) { e.printStackTrace(); }
        for (int i = 0; i < reservedSeats.size(); i++) {
            theatre.returnSeats().get(reservedSeats.get(i)).setPurchased();
            theatre.returnSeats().get(reservedSeats.get(i)).purchasedBy(Thread.currentThread().getName());
            seatsPurchased += 1;
        }
    }
}

/*
    ASCII Characters for Menu Creation

    ═ 	║ 	╒ 	╓ 	╔ 	╕ 	╖ 	╗ 	╘ 	╙ 	╚ 	╛ 	╜ 	╝ 	╞ 	╟	╠ 	╡ 	╢ 	╣ 	╤ 	╥ 	╦ 	╧ 	╨ 	╩ 	╪ 	╫ 	╬
 */
