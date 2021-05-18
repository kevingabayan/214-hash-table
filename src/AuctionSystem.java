/**
 * The <code>AuctionSystem<code> class allows the user to interact with the database by listing open auctions, making bids on open auctions, and create new auctions for different items.
 * 
 *    @author Kevin Gabayan
 *    e-mail: kevin.gabayan@stonybrook.edu
 *    Stony Brook ID: 111504873
 */
import big.data.*;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class AuctionSystem implements Serializable {
	private static AuctionTable auctionTable;
	private static String username;
	/**
	 * AuctionSystem Variables
	 * @param auctionTable
	 * The auctionTable of the system.
	 * @param username
	 * The username of the system.
	 */
	public static void menuGeneration() {
		System.out.println("Menu:\r\n" + 
				"    (D) - Import Data from URL\r\n" + 
				"    (A) - Create a New Auction\r\n" + 
				"    (B) - Bid on an Item\r\n" + 
				"    (I) - Get Info on Auction\r\n" + 
				"    (P) - Print All Auctions\r\n" + 
				"    (R) - Remove Expired Auctions\r\n" + 
				"    (T) - Let Time Pass\r\n" + 
				"    (Q) - Quit");
		System.out.println();
		System.out.print("Please select an option: ");
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Scanner input = new Scanner(System.in);
		boolean end = false;
		
		System.out.println("Starting...");
		
		auctionTable = AuctionTable.load("auction.obj");
		if(auctionTable != null) {
			System.out.println("Loading previous Auction Table...");
			System.out.println();
		}
		else {
			System.out.println("No previous auction table detected.");
			System.out.println("Creating new table...");
			System.out.println();
		}
		
		// Creates the username
		System.out.print("Please select a username: ");
		String user = input.nextLine();
		username = user;
		
		// Generates menu, asks for options
		System.out.println();
		menuGeneration();
		String selection = input.nextLine();
		
		while(end == false) {
			// Import Data from URL
			if(selection.equals("d") || selection.equals("D")) {
				System.out.print("Please enter a URL: ");
				String URL = input.nextLine();
				System.out.println();
				System.out.println("Loading...");
				auctionTable = auctionTable.buildFromURL(URL);
				if(auctionTable != null) 
					System.out.println("Auction data loaded successfully!");
				else
					System.out.println("Data retrieval unsuccessful.");
				System.out.println();
				
			}
			// Create a New Auction
			else if(selection.equals("A") || selection.equals("a")) {
				System.out.println("Creating new auction as " + user + ".");
				System.out.print("Please enter an auction ID: ");
				String id = input.nextLine();
				System.out.print("Please enter an auction time (hours): ");
				int timeRemaining = input.nextInt();
				input.nextLine();
				System.out.print("Please enter some Item Info: " );
				String info = input.nextLine();
				System.out.println();
				Auction newAuction = new Auction(id, timeRemaining, info, username);
				try {
					auctionTable.putAuction(id, newAuction);
				}
				catch(NullPointerException e) {
					System.out.println("You haven't imported any data!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					System.out.println();
					continue;
				}
				System.out.println("Auction " + id + " inserted into table.");
				System.out.println();
				
				
			}
			// Bid 
			else if(selection.equals("B") || selection.equals("b")) {
				System.out.print("Please enter an auction ID: ");
				String auctionID = input.nextLine();
				System.out.println();
				Auction auction;
				try {
					auction = auctionTable.getAuction(auctionID);
				}
				catch (NullPointerException e) {
					System.out.println("You haven't imported any data!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					System.out.println();
					continue;
				}
				try {
					if(auction.getTimeRemaining() > 0) {
						System.out.println("Auction " + auctionID + " is OPEN");
						if(auction.getCurrentBid() == 0) {
							System.out.println("    Current Bid: None");
							System.out.println();
						}
						else {
							System.out.printf("    Current Bid: $ %5.2f \n\n", auction.getCurrentBid());
						}
					
						System.out.print("What would you like to bid?: ");
						double bid = input.nextDouble();
						input.nextLine();
						auction.newBid(user, bid);
						System.out.println();
					}
					else {
						System.out.println("Auction " + auctionID + " is CLOSED");
						System.out.printf("    Current Bid: $ %5.2f \n\n", auction.getCurrentBid());
						System.out.println("You can no longer bid on this item.");
						System.out.println();
					}
				}
				catch(NullPointerException e) {
					System.out.println("There is no Auction with that ID!");
				}
				
			}
			// Get Info
			else if(selection.equals("I") || selection.equals("i")) {
				System.out.print("Please enter an auction ID: ");
				String infoID = input.nextLine();
				Auction infoauction = null;
				try {
					infoauction = auctionTable.getAuction(infoID);
				}
				catch(NullPointerException e) {
					System.out.println();
					System.out.println("You have no data imported!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					System.out.println();
					continue;
				}
				System.out.println();
				
				if(infoauction == null) {
					System.out.println("Auction not found!");
					System.out.println();
				}
				else {
					System.out.println("Auction " + infoauction.getAuctionID());
					System.out.println("    Seller: " + infoauction.getSellerName());
					System.out.println("    Buyer: " + infoauction.getBuyerName());
					System.out.println("    Time: " + infoauction.getTimeRemaining());
					System.out.println("    Info: " + infoauction.getItemInfo());
					System.out.println();
				}
				
			}
			// Print All
			else if(selection.equals("P") || selection.equals("p")) {
				System.out.println();
				try {
				auctionTable.printTable();
				}
				catch(NullPointerException e) {
					System.out.println("There's nothing to print!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					continue;
				}
				System.out.println();
				
			}
			// Remove Expired
			else if(selection.equals("R") || selection.equals("r")) {
				System.out.println("Removing expired auctions...");
				try {
				auctionTable.removeExpiredAuctions();
				}
				catch(NullPointerException e) {
					System.out.println();
					System.out.println("You haven't imported any data! Nevermind!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					System.out.println();
					continue;
				}
				System.out.println("All expired auctions removed.");
				System.out.println();
				
			}
			// Let Time Pass
			else if(selection.equals("t") || selection.equals("T")) {
				System.out.print("How many hours should pass: ");
				int hours = input.nextInt();
				input.nextLine();
				System.out.println();
				System.out.println("Time passing...");
				try {
				auctionTable.letTimePass(hours);
				}
				catch(NullPointerException e) {
					System.out.println();
					System.out.println("You haven't imported any data! Nevermind!");
					System.out.println();
					menuGeneration();
					selection = input.nextLine();
					System.out.println();
					continue;
				}
				System.out.println("Auction times updated.");
				System.out.println();
				
			}
			// Quit
			else if(selection.equals("Q") || selection.equals("q")) {
				end = true;
				System.out.println("Writing Auction Table to file...");
				System.out.println("Done!");
				auctionTable.save("auction.obj");
				System.out.println();
				System.out.println("Goodbye.");
				break;
				
			}
			menuGeneration();
			selection = input.nextLine();
			System.out.println();
		}
	}
}
