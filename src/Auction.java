/**
 * The <code>Auction<code> class represents an active auction currently in the database.
 *	  @author Kevin Gabayan
 *    e-mail: kevin.gabayan@stonybrook.edu
 *    Stony Brook ID: 111504873
 */
import java.io.Serializable;
public class Auction implements Serializable {
	private int timeRemaining;
	private double currentBid;
	private String auctionID;
	private String sellerName = "";
	private String buyerName = "";
	private String itemInfo;
	/**
	 * Auction variables
	 * @param timeRemaining
	 * The amount of time remaining for the auction.
	 * @param currentBid
	 * The amount of the current bid.
	 * @param auctionID
	 * The ID number of the auction.
	 * @param sellerName
	 * The name of the seller.
	 * @param buyerName
	 * The name of the buyer.
	 * @param itemInfo
	 * The info of the item.
	 */
	/**
	 * The following methods are the getters for each member variable.
	 * @return The member variable.
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}
	public double getCurrentBid() {
		return currentBid;
	}
	public String getAuctionID() {
		return auctionID;
	}
	public String getSellerName() {
		return sellerName;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public String getItemInfo() {
		return itemInfo;
	}
	/**
	 * An constructor for inserting a new auction.
	 */
	public Auction(String auctionID, int timeRemaining, String itemInfo, String sellerName) {
		this.auctionID = auctionID;
		this.timeRemaining = timeRemaining;
		this.itemInfo = itemInfo;
		this.sellerName = sellerName;
	}
	/**
	 * A constructor for an auction with parameters.
	 */
	public Auction(int timeRemaining, double currentBid, String auctionID, String sellerName, String
	  buyerName, String itemInfo) {
		this.timeRemaining = timeRemaining;
		this.currentBid = currentBid;
		this.auctionID = auctionID;
		this.sellerName = sellerName;
		this.buyerName = buyerName;
		this.itemInfo = itemInfo;
	}
	/**
	 * This method decrements the time remaining for the auction by the specified amount. If the time is greater
	 * than the current remaining time for the auction, the time remaining is set to 0.
	 * @param time
	 * The specified amount.
	 */
	public void decrementTimeRemaining(int time) {
		if(time > this.timeRemaining) {
			this.timeRemaining = 0;
		}
		else {
			this.timeRemaining -= time;
		}
	}
	/**
	 * This method makes a new bid on the auction. If bidAmt is larger than the currentBid, the value of currentBid is replaced by bidAmt
	 * and buyerName is replaced by bidderName.
	 * <dt><b>Preconditions:</b><dd>
	 * The auction is not closed (timeRemaining > 0)
	 * <dt><b>Postconditions:</b><dd>
	 * currentBid reflects the largest bid placed on this object. If the auction is closed, throw a ClosedAuctionException.
	 * @throws ClosedAuctionException
	 * Thrown if the auction is closed and no more bids can be placed (i.e. timeRemaining = 0)
	 * @param bidderName
	 * The name of the bid.
	 * @param bidAmt
	 * The amount of the bid.
	 */
	public void newBid(String bidderName, double bidAmt) {
		// Exception Handling
		if(this.timeRemaining <= 0) {
			System.out.println("The auction is closed and no more bids can be placed!");
			return;
		}	
		if(bidAmt > this.currentBid) {
			currentBid = bidAmt;
			buyerName = bidderName;
			System.out.println("Bid accepted.");
		}
		else {
			System.out.println("Your bid is not high enough. Bid declined.");
		}
	}
	/**
	 * This method returns the string of data members in tabular form.
	 * @return The string of data members in tabular form.
	 */
	public String toString() {
		String tabularForm = "";
		if(buyerName.equals("")) {
			tabularForm += String.format("%11s |            | %-22s|  %-23s|%4d hours | %-100s",
			  this.auctionID, this.sellerName, this.buyerName, this.timeRemaining, this.itemInfo);	
		}
		else {
			tabularForm += String.format("%11s | $%9.2f | %-22s|  %-23s|%4d hours | %-100s",
			  this.auctionID, this.currentBid, this.sellerName, this.buyerName, this.timeRemaining, this.itemInfo);	
		}
		return tabularForm;
	}
	
}
