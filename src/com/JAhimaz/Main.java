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
        // The end-user selects the number of SEATS they which to have in the Theatre (100-200)
        System.out.print("\nPlease Enter The Number of SEATS in the Theatre: ");
        // This sends it through a custom library called InputHandling that checks if the input
        // is an Integer and is within the given range.
        int numberOfSeats = InputHandling.Integer(100, 200);
        System.out.print("Number of SEATS set to: " + numberOfSeats + "\n");
        // Similarily the end-user selects the number of USERS that will be making an attempt
        // to buy seats in the Theatre.
        System.out.print("\nPlease Enter The Number of USERS in the Theatre: ");
        int numberOfUsers = InputHandling.Integer(100, 2000);
        System.out.print("Number of USERS set to: " + numberOfUsers + "\n");

        // These variables are then passed down to the booking scenario.
        bookingScenario(numberOfSeats, numberOfUsers);
    }

    public static void bookingScenario(int numberOfSeats, int numberOfUsers){
        // A Theatre object is initialised, since there is only 1 Theatre in the cinema.
        Theatre theatre = new Theatre(numberOfSeats, "Sunway Pyramid TGV");
        // An executor service is used as there is a large number of threads required.
        // This is assigned by the number of USERS inputted by the end-user.
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        // A for loop is run creating the user class and executing the run() statement
        // of each USER thread simultaneously.
        for(int i = 0; i < numberOfUsers; i++){
            Runnable user = new User(theatre);
            executor.execute(user);
        }
        // Once all USERS have completed, or the theatre is full, the executor will shutdown.
        executor.shutdown();  
        // The following While statement is to keep threads Alive.
        while (!executor.isTerminated()) {   }  

        // This for loop prints out all the seat numbers (+1), so it does not start at 0, and
        // checks whether or not the seat has been purchased/reserved or is available. This is
        // whats used for verifying the purchase of each seat.
        System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
        System.out.println("\n                               Seat Verification\n");
        System.out.println("\n════════════════════════════════════════════════════════════════════════════════\n\n");
        for (int i = 0; i < theatre.getNumberOfSeats(); i++) {
            System.out.println("Seat Number (" + (theatre.returnSeats().get(i).getSeatNumber()) + ") : " + theatre.returnSeats().get(i).getStatusOfSeat() + " | Owned By: " + theatre.returnSeats().get(i).getPurchaser());
        }
        
        if(!theatre.isAvailableSeats()){
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\n                           Theatre is Fully Booked\n");
            System.out.println("                           Total Reserved Seats: " + theatre.getTotalReservedSeats());
            System.out.println("                           Total Purchased Seats: " + theatre.getTotalPurchasedSeats());
            System.out.println("\n════════════════════════════════════════════════════════════════════════════════\n\n");
        }else{
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\n                  Theatre Still Has Remaining Available Seats\n");
            System.out.println("════════════════════════════════════════════════════════════════════════════════\n\n");
        }
    }
}

class Theatre {

    private String theatreName;
    private int numberOfSeats;
    private List<Seat> seats = new ArrayList<Seat>();

    // For Debugging
    private int totalPurchasedSeats = 0;
    private int totalReservedSeats = 0;

    Theatre(int numberOfSeats, String name){
        theatreName = name;
        this.numberOfSeats = numberOfSeats;
        for (int i = 0; i < numberOfSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public synchronized List<Seat> returnSeats(){ return seats; }

    public synchronized Boolean isAvailableSeats(){
        for (Seat seat : seats) {
            if(seat.getReservationStatus() == false){
                return true;
            }
        }

        return false;
    }

    // SETTERS
    public synchronized void setTotalPurchasedSeats(int add){ totalPurchasedSeats += add; }
    public synchronized void setTotalReservedSeats(int add){ totalReservedSeats += add; }

    // GETTERS
    public int getNumberOfSeats(){ return numberOfSeats; }
    public String getTheatreName(){ return theatreName; }
    public int getTotalPurchasedSeats(){ return totalPurchasedSeats; }
    public int getTotalReservedSeats(){ return totalReservedSeats; }
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
        // System.out.println(seatsToPurchase);
    }

    public synchronized void run() {

        // Loop through each seat

        for (int j = 0; j < theatre.getNumberOfSeats(); j++) {
            // if the User reaches the maximum amount of seats he wished to purchase
            if(reservedSeats.size() == seatsToPurchase || !theatre.isAvailableSeats()){ break; }
            // if seat is reserved continue
            if(theatre.returnSeats().get(j).getReservationStatus() == true){
                continue;
            }else{
                Boolean reserved = theatre.returnSeats().get(j).setReserved();
                if(reserved){
                    reservedSeats.add(theatre.returnSeats().get(j).getSeatNumber());
                    System.out.println(Thread.currentThread().getName() + " Reserved Seat Number (" + (theatre.returnSeats().get(j).getSeatNumber()) + ")");
                }
                int seatNumber = j;
                for(int x = 0; x < seatsToPurchase; x++){
                    if(reservedSeats.size() == seatsToPurchase || !theatre.isAvailableSeats()){
                        break;
                    }
                    
                    if(theatre.returnSeats().get(seatNumber).getReservationStatus() == true){
                        x -= 1;
                    }else{
                        Boolean seatReserved = theatre.returnSeats().get(seatNumber).setReserved();
                        if(seatReserved){
                            reservedSeats.add(theatre.returnSeats().get(seatNumber).getSeatNumber());
                            System.out.println(Thread.currentThread().getName() + " Reserved Seat Number (" + (theatre.returnSeats().get(seatNumber).getSeatNumber()) + ")");
                        }else{
                            x -= 1;
                        }
                    }
                    seatNumber += 1;
                }
                j = seatNumber;
            }
        }


        purchaseSeats();

        if(seatsPurchased > 0){
            System.out.println(Thread.currentThread().getName() + " purchased " + seatsPurchased + " seat(s)." + " | Reserved: " + reservedSeats);
            theatre.setTotalReservedSeats(reservedSeats.size());
            theatre.setTotalPurchasedSeats(seatsPurchased);
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
