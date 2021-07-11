package com.JAhimaz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

import com.JAhimaz.ioh.InputHandling; // Custom Input Handling Library

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
        System.out.println("════════════════════════════════════════════════════════════════════════════════\n\n");
        for (int i = 0; i < theatre.getNumberOfSeats(); i++) {
            // This will loop through all the seats in the theatre and return their current status as well as who purchased the seat.
            System.out.println("Seat Number (" + (theatre.returnSeats().get(i).getSeatNumber()) + ") : " + theatre.returnSeats().get(i).getStatusOfSeat() + " | Owned By: " + theatre.returnSeats().get(i).getPurchaser());
        }
        
        // The final console print for the end-user to show that the total reserved/purchased seats is the same as the total number
        // of initial seats, this can be backchecked by going through the number of seats provided during the program run.
        if(!theatre.isAvailableSeats()){
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\n                           Theatre is Fully Booked\n");
            System.out.println("                           Total Reserved Seats: " + theatre.getTotalReservedSeats());
            System.out.println("                           Total Purchased Seats: " + theatre.getTotalPurchasedSeats());
            System.out.println("\n════════════════════════════════════════════════════════════════════════════════\n\n");
        }else{
        // If there is, by some chance, more seats available, this will print. If this prints, the number of SEATS is greater
        // than the number of USERS and each user has chosen to only buy 1 SEAT. Rare, but possible if done with this scenario.
        // For example, 100 USERS, 200 SEATS. All users buy 2 seats except one user buys 1 seat. total would be 199/200.
            System.out.println("\n\n════════════════════════════════════════════════════════════════════════════════");
            System.out.println("\n                  Theatre Still Has Remaining Available Seats\n");
            System.out.println("════════════════════════════════════════════════════════════════════════════════\n\n");
        }
    }
}

// The Theatre Class
class Theatre {

    // Variable declartion for the Theatre

    private String theatreName; // The name of the theatre, this can be used for differentiating if more were added.
    private int numberOfSeats; // The number of seats in the theatre, used to compare later on (Similar to seats.size())
    private List<Seat> seats = new ArrayList<Seat>(); // The SEATS, an ArrayList of SEAT object.

    // For Debugging & Verification
    private int totalPurchasedSeats = 0;
    private int totalReservedSeats = 0;

    // Constructor for the Theatre (Literal construction).
    Theatre(int numberOfSeats, String name){
        theatreName = name;
        this.numberOfSeats = numberOfSeats;
        for (int i = 0; i < numberOfSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    // Returns the List of Seats for accessing during the run() execution of each USER.
    public synchronized List<Seat> returnSeats(){ return seats; }

    // Used to check if all the seats in the List of Seats is reserved.
    public synchronized Boolean isAvailableSeats(){
        for (Seat seat : seats) {
            // If even one seat is unreserved, it will return true (Meaning there's available).
            if(seat.getReservationStatus() == false){
                return true;
            }
        }
        // Else, all seats are booked.
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

// The Seat Class
class Seat {

    // Variable Declaration for the Seat

    private Boolean reserved = false; // This boolean represents if the seat had been reserved.
    private Boolean purchased = false; // This boolean represents if the seat had been purchased.
    private String purchasedBy; // This String represents the Thread/USER who purchased the seat.
    private int seatNumber; // This is the assigned seat number.

    // Constructor
    Seat(int seatNumber){
        this.seatNumber = seatNumber;
    }

    // This is a synchronised function to Set the seat as reserve. This was the solution to when
    // users were able to reserve a seat that had already been reserved. Without this, there would
    // be duplicate seat reserves.
    public synchronized Boolean setReserved(){
        if(reserved == false){
            reserved = true;
            return true;
        }else{
            return false;
        }
    }

    // SETTERS
    public synchronized void setPurchased(){ purchased = true; }
    public synchronized void purchasedBy(String purchaser){ purchasedBy = purchaser; }

    // GETTERS
    public synchronized Boolean getReservationStatus(){ return reserved; }
    public synchronized String getPurchaser(){ return purchasedBy; }
    public int getSeatNumber(){ return seatNumber; }

    // Returning the current status of the seat for viewing of the end-user.
    public synchronized String getStatusOfSeat(){
        if(reserved == true && purchased == false){ return "Reserved"; }
        if(reserved == true && purchased == true){ return "Purchased"; }
        return "Available";
    }

}

// The USER class
class User implements Runnable {

    // Variable Declaration for the User

    // private String nameOfPerson; // This was removed as it is not necessary.
    private Theatre theatre; // Referencing the theatre object.
    private int seatsToPurchase; // The number of seats the user wishes to purchase.
    private int seatsPurchased = 0; // The number of seats the user has purchased.

    // An ArrayList of seats purchased by the user
    // Possibility to store the whole SEAT, but since just the seat number was
    // required and is used for nothing else than purchasing/verification, it
    // was pointless to store the whole SEAT objects inside.
    private List<Integer> reservedSeats = new ArrayList<Integer>(); 

    // Constructor
    User(Theatre theatre){
        // nameOfPerson = name; // Possibly a random generated value
        this.theatre = theatre; // Assigning the theatre object
        // Randomly choosing if the user wants to purchase between 1 to 3 seats.
        seatsToPurchase = (int) (Math.random() * ((3 - 1) + 1) + 1); 
        // seatsToPurchase = 1; // Test case scenario to check if available seats are possible.

        // System.out.println(seatsToPurchase); // Debugging purposes.
    }

    // the run execution.
    public synchronized void run() {

        // Begins by looping through the number of seats in the List of Seats provided by theatre.
        for (int j = 0; j < theatre.getNumberOfSeats(); j++) {
            // If the User reaches the maximum amount of seats he wished to purchase (by reserving) or
            // the theatre is out of available seats, it will break the for loop. Ending all users from
            // reserving further
            if(reservedSeats.size() == seatsToPurchase || !theatre.isAvailableSeats()){ break; }
            // If the current seat has the status of reserved, it will continue on to the next seat.
            if(theatre.returnSeats().get(j).getReservationStatus() == true){
                continue;
            }else{
                // If the current seat does not have a status of reserved, the USER will quickly set the
                // seat as reserved, preventing other users from reserving as well, however, this needs to
                // go through an initial check first, so there is a chance that this seat was already
                // reserved. (Multithreading annoyances).
                Boolean reserved = theatre.returnSeats().get(j).setReserved();
                if(reserved){
                    // Add the seat number to the reserved seat numbers list.
                    reservedSeats.add(theatre.returnSeats().get(j).getSeatNumber());
                    // Print that the current USER has reserved the seat number of x
                    System.out.println(Thread.currentThread().getName() + " Reserved Seat Number (" + (theatre.returnSeats().get(j).getSeatNumber()) + ")");
                }

                // Either way if that seat was reserved or not, the USER will make a quick attempt to buy the upcoming seats.
                // This also means, if he was to get the initial reserve seat from above, he can purchase more in a row to meet his demand for the number of
                // Seats.
                int seatNumber = j; // We assign the current seat number to a value.

                // This loop is for the user to purchase the number of seats that he needed.
                for(int x = 0; x < seatsToPurchase; x++){

                    // If there are no more seats, or the user has gotten all the seats he needs, this will break the loop, create a 
                    // new interation on the outer loop and break that as well as the first condition is also checking if he meets
                    // his reserve seat needs or if the theatre is full.
                    if(reservedSeats.size() == seatsToPurchase || !theatre.isAvailableSeats()){
                        break;
                    }
                    
                    // If the seat he is currently checking is reserved, -1 from the current loop as he still needs to purchase another seat.
                    if(theatre.returnSeats().get(seatNumber).getReservationStatus() == true){
                        x -= 1;
                    // If he is able to get the seat, attempt to reserve it.
                    }else{
                        // Ofcourse, another check is necessary similar to booking the first seat, this is because there is a chance in multithreading that
                        // another user has already reserved the seat the moment he attempts to, this acts as a makeshift lock check.
                        Boolean seatReserved = theatre.returnSeats().get(seatNumber).setReserved();
                        if(seatReserved){
                            // Add the seat number to the reserved seat numbers list.
                            reservedSeats.add(theatre.returnSeats().get(seatNumber).getSeatNumber());
                            // Print that the current USER has reserved the seat number of x
                            System.out.println(Thread.currentThread().getName() + " Reserved Seat Number (" + (theatre.returnSeats().get(seatNumber).getSeatNumber()) + ")");
                        }else{
                            // Again, if the user was unable to acquire that seat, it will deiterate, to allow him to attempt to purchase another seat.
                            x -= 1;
                        }
                    }
                    // Will move on to the next seat, whether he reserves it or not, unless the break arguements are met.
                    seatNumber += 1;
                }

                // Not really necessary, but this reassigns the current J value to the seat that was last looked at, to prevent the USER from going through the whole
                // list again knowing the statuses of the seat previously.
                j = seatNumber;
            }
        }

        // Once the theatre is booked, and/or the USER has reserved the seats he needs, it will go through the list and purchase all the seats for the user.
        // The if statement is just to check if a USER had managed to reserve a seat, works without, but will unnecessary attempt to purchase seats for users,
        // who do not have any reserved seats.
        if(reservedSeats.size() > 0){ purchaseSeats(); }

        // Upon purchasing, a prompt will display how many seats the user had purchased as well as the seat numbers in reserves.
        if(seatsPurchased > 0){
            System.out.println(Thread.currentThread().getName() + " purchased " + seatsPurchased + " seat(s)." + " | Reserved: " + reservedSeats);
            theatre.setTotalReservedSeats(reservedSeats.size()); // Will add to the number of total reserved seats for debugging
            theatre.setTotalPurchasedSeats(seatsPurchased); // Will add to the number of total purchased seats for debugging
        }

    }

    private void purchaseSeats(){
        // The user has a short delay between 500-1000 ms before the purchase goes through.
        try {  Thread.sleep(((int) (Math.random() * ((1000 - 500) + 500) + 1)));  } catch (InterruptedException e) { e.printStackTrace(); }
        // Upon which all seats in his reserve will be purchased.
        for (int i = 0; i < reservedSeats.size(); i++) {
            theatre.returnSeats().get(reservedSeats.get(i)).setPurchased(); // Changes the status of the seat from Reserved to Purchased
            theatre.returnSeats().get(reservedSeats.get(i)).purchasedBy(Thread.currentThread().getName()); // Sets the purchased by to the thread name.
            seatsPurchased += 1;
        }
    }
}

/*
    Movie Theatre Seat Reservation System
    https://github.com/JAhimaz/Movie-Theatre-Seat-Reservation

    07/11/2021

    ASCII Characters for Menu Creation

    ═ 	║ 	╒ 	╓ 	╔ 	╕ 	╖ 	╗ 	╘ 	╙ 	╚ 	╛ 	╜ 	╝ 	╞ 	╟	╠ 	╡ 	╢ 	╣ 	╤ 	╥ 	╦ 	╧ 	╨ 	╩ 	╪ 	╫ 	╬
 */
