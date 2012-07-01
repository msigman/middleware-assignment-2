import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * The Seller class publishes (available item, bid update, sale finalized)
 * and register subscriptions (receive bid).
 * The Seller handles bid automatically
 * 
 */

/**
 * @author Anh Luong
 *
 */
public class Seller {

	public static final int maximumItem = 50;
	/**
	 * @param args
	 */
	protected int sellerId_;
	protected HashMap<Integer,AvailableItem> availableItemList_;
	protected HashMap<Integer,AvailableItem> bidUpdateItemList_;
	protected HashMap<Integer,AvailableItem> saleFinalizedItemList_;
	
	public Seller(int sellerId)
	{
		sellerId_ = sellerId;
		availableItemList_ = new HashMap<Integer,AvailableItem>(maximumItem);
		bidUpdateItemList_ = new HashMap<Integer,AvailableItem>(maximumItem);
		saleFinalizedItemList_ = new HashMap<Integer,AvailableItem>(maximumItem);
	}
	
	/**
	 * register a buyer to be notified when an available item is produced
	 * This should be handle by the broker, instead, the seller only knows
	 * which item is subscribed by a buyer
	 * 
	 *  Return false if cannot subscribe because the item is not published
	 *  by this seller
	 */
	public boolean addAvailableItemList(AvailableItem item)
	{
		if (searchAvailableItemList(item.getId()))
			return false;
		else
		{
			availableItemList_.put(item.getId(), item);
			return true;
		}
	}
	
	public boolean removeAvailableItemList(int itemId)
	{
		if (searchAvailableItemList(itemId))
		{
			// if item is found in the availableItemList_
			availableItemList_.remove(itemId);
			return true;
		}
		return false;
	}

	
	public boolean searchAvailableItemList(int itemId)
	{
		return availableItemList_.containsKey(itemId); 
	}
	
	public AvailableItem getAvailableItem(int itemId)
	{
		// MUST call searchAvailableItemList to verify 
		// this item is in the availableItemList_ before call this function
		return availableItemList_.get(itemId); 
	}

	public boolean addBidUpdateItemList(int itemId)
	{
		if (searchAvailableItemList(itemId))
		{
			if (searchBidUpdateItemList(itemId))
				return true; // if the item is already in the bidUpdateItemList_
			else
			{
				// if the item is NOT already in the bidUpdateItemList_, then add it
				AvailableItem item = getAvailableItem(itemId);
				bidUpdateItemList_.put(item.getId(), item);	
				return true;
			}
		}
		return false;
	}
	
	public boolean searchBidUpdateItemList(int itemId)
	{
		return bidUpdateItemList_.containsKey(itemId); 
	}
	
	public boolean searchSaleFinalizedItemList(int itemId)
	{
		return saleFinalizedItemList_.containsKey(itemId); 
	}
	
	public void bidHandler(int itemId, double newBid, int buyerId)
	{
		return;
	}
	
	public void printAvailableItemList()
	{
		Iterator it = availableItemList_.keySet().iterator();
		while (it.hasNext())
		{
			AvailableItem it1 = availableItemList_.get(it.next());
			System.out.format("Id %d with name %s",it1.getId(), it1.getName());
		}
	}
	
	public static void main(String[] args) {
		Seller sel1 = new Seller(0);
		AvailableItem it1 = new AvailableItem(50,"chair",20); 
		sel1.removeAvailableItemList(it1.getId());
		sel1.printAvailableItemList();
		sel1.addAvailableItemList(it1);
		sel1.printAvailableItemList();

	}

}
