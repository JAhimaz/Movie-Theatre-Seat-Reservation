package com.JAhimaz.ioh;

import java.util.Scanner;

public class InputHandling {
	
	static Scanner input = new Scanner(System.in);
	
	public static int Integer(int minChoice, int maxChoice) {
		int choice, error = 0;
		do {
			if(error > 0) {
				System.out.println("\nPlease Enter A Number From " + minChoice + " to " + maxChoice);
				sleep(1000);
			}
		    System.out.print("\n> ");
		    while (!input.hasNextInt()) {
		        System.out.println("\nPlease Input Numbers Only!");
				sleep(1000);
		        System.out.print("\n> ");
		        input.next(); // this is important!
		    }
		    choice = input.nextInt();
		    error++;
		} while (!(choice >= minChoice && choice <= maxChoice));
		
		return choice;
	}

	public static boolean Boolean(){
		int error = 0;
		boolean valid = false;
		String choice = "undefinedvalue";

		do {

		} while (false);

//		if(choice == "Y" || choice == "y"){
//			return true;
//		}else if(choice == "N" || choice == "n"){
//			return false;
//		}

		return true;
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

/*
╔═══════════════════════════════════════════════╗
║     Simple Integer Input Handling System -    ║
╚═══════════════════════════════════════════════╝

Usage: InputHandling.Integer(Minimum, Maximum);

Description: Easily Validates input of Integers within a range.

Created by Joshua Ahimaz

*/