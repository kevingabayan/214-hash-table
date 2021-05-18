/**
 * The <code>AuctionTable<code> class represents the database of open auctions stored in a hash table to provide
 * constant time insertion and deletion. The auctionID will be the key to the corresponding Auction object.
 *    @author Kevin Gabayan
 *    e-mail: kevin.gabayan@stonybrook.edu
 *    Stony Brook ID: 111504873
 */
import big.data.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AuctionTable implements Serializable {
	private static Hashtable auctionTable = new Hashtable();
	/**
	 * This is the constructor used for the buildfromURL auction table.
	 */
	public AuctionTable() {
		Hashtable auctionTable = this.auctionTable;
	}
	/**
	 * This method uses the BigData library to construct an AuctionTable from a remote data source.
	 * @param URL
	 * The URL to be used.
	 * <dt><b>Preconditions:</b><dd>
	 * The URL represents a data source that can be connected to using the BigData library.
	 * The data source has proper syntax.
	 * @return
	 * The AuctionTable constructed from the remote data source.
	 * @throws IllegalArgumentException
	 * Thrown if the URL does not represent a valid data source.
	 */
	public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException {
		// Takes the Load
		DataSource ds = null;
		ds = DataSource.connect(URL).load();
		
		// Initializes Values
		String[] sellerName;
		try {
			sellerName = ds.fetchStringArray("listing/seller_info/seller_name");
		}
		catch(big.data.DataSourceException e) {
			return null;
		}
		String[] currentBid = ds.fetchStringArray("listing/auction_info/current_bid");
		String[] timeLeft = ds.fetchStringArray("listing/auction_info/time_left");
		String[] idNum = ds.fetchStringArray("listing/auction_info/id_num");
		String[] bidderName = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
		String[] informationMemory = ds.fetchStringArray("listing/item_info/memory");
		String[] informationHardDrive = ds.fetchStringArray("listing/item_info/hard_drive");
		String[] informationCpu = ds.fetchStringArray("listing/item_info/cpu");
		
		// Creates the Loop
		for(int i = 0; i < sellerName.length; i++) {
			String information = informationCpu[i] + "-" + informationMemory[i] + " - " + informationHardDrive[i];
			// Examines timeLeft string and converts it into the proper amount of hours.
			int days;
			int hours;
			int timeRemaining = 0;
			String indexSubstring;
			if(timeLeft[i].contains("hour")) {
				int indexHours = timeLeft[i].indexOf("hour");
				if(indexHours == 2)
					hours = Integer.parseInt(timeLeft[i].substring(indexHours-2, indexHours-1));
				else {
					indexSubstring = timeLeft[i].substring(indexHours-3, indexHours-1);
					indexSubstring = indexSubstring.replaceAll(" ", "");
					hours = Integer.parseInt(indexSubstring);
				}
				timeRemaining += hours;
			}
			if(timeLeft[i].contains("hr")) {
				int indexHours = timeLeft[i].indexOf("hr");
				if(indexHours == 2)
					hours = Integer.parseInt(timeLeft[i].substring(indexHours-2, indexHours-1));
				else {
					indexSubstring = timeLeft[i].substring(indexHours-3, indexHours-1);
					indexSubstring = indexSubstring.replaceAll(" ", "");
					hours = Integer.parseInt(indexSubstring);
				}
				timeRemaining += hours;
			}
			if(timeLeft[i].contains("day")) {
				int indexDays = timeLeft[i].indexOf("day");
				if(indexDays == 2)
					days = Integer.parseInt(timeLeft[i].substring(indexDays-2, indexDays-1));
				else {
					indexSubstring = timeLeft[i].substring(indexDays-3, indexDays-1);
					indexSubstring = indexSubstring.replaceAll(" ", "");
					days = Integer.parseInt(indexSubstring);
				}
				timeRemaining += days * 24;
			}
			// Converts presentBid to a proper double without the dollar signs and commas. 
			if(currentBid[i].contains(","))
				currentBid[i] = currentBid[i].replaceAll(",", "");
			double presentBid = Double.parseDouble(currentBid[i].substring(1));
			// Constructs the Auction Table
			Auction toAdd = new Auction(timeRemaining, presentBid, idNum[i], sellerName[i], bidderName[i], information);
			auctionTable.put(idNum[i], toAdd);
		}
		AuctionTable autumnLeaf = new AuctionTable();
		return autumnLeaf;
	}
	/**
	 * This method manually posts an auction and adds it onto the table.
	 * @param auctionID 
	 * The unique key for the object
	 * @param auction
	 * The auction to insert into the table with the corresponding auctionID
	 * <dt><b>Postconditions:</b><dd>
	 * The item will be added to the table if all given parameters are correct.
	 * @throws IllegalArgumentException
	 * If the given auctionID is already stored in the table. 
	 */
	public void putAuction(String auctionID, Auction auction) {
		if(auctionTable.contains(auction) && auctionTable.containsKey(auctionID)) {
			System.out.println("This auctionID is already stored in the table!");
			return;
		}
		auctionTable.put(auctionID, auction);
	}
	/**
	 * This method retrieves the Auction information with the given ID.
	 * @param auctionID
	 * The unique key for the object
	 * @return
	 * An Auction object with the given key or null if otherwise.
	 */
	public Auction getAuction(String auctionID) {
		return (Auction)this.auctionTable.get(auctionID);
	}
	/**
	 * This method simulates the passing of time, and decreases the amount of time remaining of all objects by 
	 * a specified amount. The value cannot go below 0.
	 * @param numHours
	 * The number of hours to decrease the timeRemaining value by.
	 * <dt><b>Postconditions:</b><dd>
	 * Every auction in the table have their timeRemaining timer decreased. If the original value is less than
	 * the decreased value, set the value to 0.
	 * @throws IllegalArgumentException
	 * If the given number of hours is non positive.
	 */
	public void letTimePass(int numHours) {
		if(numHours < 0) {
			System.out.println("That makes no sense!");
			return;
		}
		// Iterator and Set Creation
		Set<String> keyset = auctionTable.keySet();
		Iterator<String> iterator = keyset.iterator();
		String currentID;
		while(iterator.hasNext()) {
			currentID = iterator.next();
			Auction cool = (Auction) auctionTable.get(currentID);
			cool.decrementTimeRemaining(numHours);
		}
	}
	/**
	 * This method iterates over all auction objects in the table and removes them if they are expired.
	 * <dt><b>Postconditions:</b><dd>
	 * Only open Auction remains in the table.
	 */
	public void removeExpiredAuctions() {
		Set<String> keyset = auctionTable.keySet();
		Iterator<String> iterator = keyset.iterator();
		String currentID;
		while(iterator.hasNext()) {
			currentID = iterator.next();
			Auction cool = (Auction) auctionTable.get(currentID);
			if(cool.getTimeRemaining() <= 0) {
				iterator.remove();
			}
		}
	}
	/**
	 * This method prints the AuctionTable in tabular form.
	 */
	public void printTable() {
		System.out.println(" Auction ID |      Bid   |        Seller         |          Buyer          |    Time   | Item Info ");
		System.out.println("===================================================================================================================================");
		Set<String> keyset = auctionTable.keySet();
		Iterator<String> iterator = keyset.iterator();
		String currentID;
		while(iterator.hasNext()) {
			currentID = iterator.next();
			Auction cool = (Auction)auctionTable.get(currentID);
			System.out.println(cool.toString());
		}
	}
	/**
	 * This is the save method recommended by TA Johnny So on the Piazza forums.
	 * @param filename
	 * @throws IOException 
	 */
	public void save(String filename) throws IOException {
		FileOutputStream file = new FileOutputStream("auction.obj");
		ObjectOutputStream outStream = new ObjectOutputStream(file);
		outStream.writeObject(this.auctionTable);
	}
	/**
	 * This is the load method recommended by TA Johnny So on the piazza forums.
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static AuctionTable load(String filename) throws IOException, ClassNotFoundException {
		FileInputStream file = null;
		try {
			file = new FileInputStream("auction.obj");
		}
		catch(FileNotFoundException e) {
			return null;
		}
		ObjectInputStream inStream = new ObjectInputStream(file);
		AuctionTable auctions = new AuctionTable();
		Hashtable easyMoney;
		easyMoney = (Hashtable) inStream.readObject();
		
		auctions.auctionTable = easyMoney;
		return auctions;
	}	
}
