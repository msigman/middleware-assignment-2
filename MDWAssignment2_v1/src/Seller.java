import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
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
public class Seller extends Thread{

	public static final int maximumItem = 50;
	
	protected int port_;
	protected int sellerId_ = -1;
	protected InetAddress address_;
	protected DatagramSocket mySocket_ = null;
	protected boolean openConnection = true;
	protected BufferedReader input_;

	protected HashMap<Integer,SellerAvailableItem> availableItemList_; // key is itemID
	protected HashMap<Integer,BidUpdate> bidUpdateItemList_; // key is itemID
	protected HashMap<Integer,AvailableItem> saleFinalizedItemList_; // key is itemID
	
	public Seller(int brokerPort)
	{
		sellerId_ = -1;
		availableItemList_ = new HashMap<Integer,SellerAvailableItem>(maximumItem);
		bidUpdateItemList_ = new HashMap<Integer,BidUpdate>(maximumItem);
		saleFinalizedItemList_ = new HashMap<Integer,AvailableItem>(maximumItem);
		
		input_ = new BufferedReader(new InputStreamReader(System.in));
		port_ = brokerPort;

		try
		{
			address_ = InetAddress.getByName("localhost");
			
			// get a datagram socket
			mySocket_ = new DatagramSocket();
		}
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	public synchronized void setSellerId (int sellerId)
	{
		sellerId_ = sellerId;
	}
	
	public synchronized int getSellerId ()
	{
		return sellerId_;
	}

	/*
	public Seller(int sellerId, int brokerPort)
	{
		sellerId_ = sellerId;
		availableItemList_ = new HashMap<Integer,SellerAvailableItem>(maximumItem);
		bidUpdateItemList_ = new HashMap<Integer,BidUpdate>(maximumItem);
		saleFinalizedItemList_ = new HashMap<Integer,AvailableItem>(maximumItem);
		
		input_ = new BufferedReader(new InputStreamReader(System.in));
		port_ = brokerPort;

		try
		{
			address_ = InetAddress.getByName("localhost");
			
			// get a datagram socket
			mySocket_ = new DatagramSocket();
		}
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	*/
	
	
	public synchronized String processMessage(String message)
	{
		
		StringTokenizer st = new StringTokenizer(message);
		
		//get command name
		String command = st.nextToken();
		if (command.equalsIgnoreCase("connected"))
		{
			int sellerId = Integer.parseInt(st.nextToken());
			notifyValidSellerId(sellerId);
			System.out.println("Seller " + sellerId + " receives connected str from broker");
			return command;
		}
	    else
	    {
	    	return Constants.INVALID_COMMAND_ERROR;
	    }
		/*
		if (command.equalsIgnoreCase("available"))
		{
			if (st.countTokens() == 2)
			{
				// get the name
				String name = st.nextToken();
				int count = 0;
				// get the number
				String number = st.nextToken();
				try
				{
					count = Integer.parseInt(number);
				}
				catch (NumberFormatException e)
				{
					return INVALID_PARAMETER_ERROR;
				}
				return reserve(name, count);
			}
			else
			{
				return INVALID_PARAMETER_ERROR;
			}			
		}
		else if (command.equalsIgnoreCase("publish"))
		{
			if (st.countTokens() == 1)
			{
				// get the name
				String name = st.nextToken();
				return search(name);
			}
			else
			{
				return INVALID_PARAMETER_ERROR;
			}				
		}
		else if (command.equalsIgnoreCase("bid"))
		{
			if (st.countTokens() == 1)
			{
				// get the name
				String name = st.nextToken();
				return delete(name);
			}
			else
			{
				return INVALID_PARAMETER_ERROR;
			}							
		}
	    else if (command.equalsIgnoreCase("quit"))
	    {
			if (st.countTokens() == 0)
			{
				return getInfo();
			}
			else
			{
				return INVALID_PARAMETER_ERROR;
			}				
	    }
	    else
	    {
	    	return INVALID_COMMAND_ERROR;
	    }
	    */
	}


	public synchronized void waitTilValidSellerId()
	{
		while (sellerId_ == -1)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e) {}
		}
	}

	public synchronized void notifyValidSellerId(int sellerId)
	{
		sellerId_ = sellerId;
		notifyAll();
	}
	
	/**
	 * register a buyer to be notified when an available item is produced
	 * This should be handle by the broker, instead, the seller only knows
	 * which item is subscribed by a buyer
	 * 
	 *  Return false if cannot subscribe because the item is not published
	 *  by this seller
	 */
	public boolean addAvailableItemList(SellerAvailableItem item)
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
	
	public SellerAvailableItem getAvailableItem(int itemId)
	{
		// MUST call searchAvailableItemList to verify 
		// this item is in the availableItemList_ before call this function
		return availableItemList_.get(itemId); 
	}

	public boolean addBidUpdateItemList(int itemId)
	{
		/*
		if (searchAvailableItemList(itemId))
		{
			if (searchBidUpdateItemList(itemId))
				return true; // if the item is already in the bidUpdateItemList_
			else
			{
				// if the item is NOT already in the bidUpdateItemList_, then add it
				SellerAvailableItem item = getAvailableItem(itemId);

				bidUpdateItemList_.put(item.getId(), item);	
				return true;
			}
		}
		*/
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
	
	public void bidHandler(int itemId, int newBid, int buyerId)
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
	
	public String sendBrokerThenGetResponse (String sentMsg)
	{
        String receivedMsg = "";
		byte[] buf = new byte[256];
        buf = sentMsg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address_, port_);

        try
        {
        	mySocket_.send(packet);
        	buf = new byte[256];
        	
            // get response
            packet = new DatagramPacket(buf, buf.length);
            mySocket_.receive(packet);
            
    	    // check response
            receivedMsg = new String(packet.getData(), 0, packet.getLength());
            
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
		return receivedMsg;
	}
	
	public void run()
	{
		processMessage(sendBrokerThenGetResponse("connect seller"));
        while (openConnection) 
        {
        	
        }
	}
	
	/*
	public static void main(String[] args) {
		Seller sel1 = new Seller(0);
		AvailableItem it1 = new AvailableItem(50,"chair",20); 
		sel1.removeAvailableItemList(it1.getId());
		sel1.printAvailableItemList();
		sel1.addAvailableItemList(it1);
		sel1.printAvailableItemList();

	}
	*/

}
