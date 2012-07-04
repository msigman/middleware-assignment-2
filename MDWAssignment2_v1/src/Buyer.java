import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

/**
 * The Buyer class publishes (bid)
 * and register subscriptions (interest, receive bid, interest bid update, item sold).
 * The Buyer can buy in 2 modes: (1) user-driven mode and (2) automatic mode.
 * 
 */

/**
 * @author Anh Luong
 *
 */
public class Buyer extends Thread{

	public static final int maximumItem = 50;

	protected int port_;
	protected int buyerId_ = -1;
	protected int interestId_ = 0;
	protected InetAddress address_;
	protected DatagramSocket mySocket_ = null;
	protected boolean openConnection = true;
	protected BufferedReader input_;
	
	
	//protected HashMap<Integer,Interest> interestList_; // at the moment, use interestId as key
	protected List<Interest> interestList_; 
	protected HashMap<Integer,Bid> bidList_; // key: itemId

	public Buyer(int brokerPort)
	{
		buyerId_ = -1;
		interestList_ = new ArrayList<Interest>();
		bidList_ = new HashMap<Integer,Bid>(maximumItem);
		
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
	
	public synchronized void waitTilValidBuyerId()
	{
		while (buyerId_ == -1)
		{
			try
			{
				wait();
			}
			catch (InterruptedException e) {}
		}
	}

	public synchronized void notifyValidBuyerId(int buyerId)
	{
		buyerId_ = buyerId;
		notifyAll();
	}
	
	public synchronized String processMessage(String message)
	{
		
		StringTokenizer st = new StringTokenizer(message);
		
		//get command name
		String command = st.nextToken();
		if (command.equalsIgnoreCase("connected"))
		{
			int buyerId = Integer.parseInt(st.nextToken());
			notifyValidBuyerId(buyerId);
			System.out.println("Buyer " + buyerId + " receives connected str from broker");
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
	
	/*
	public boolean addInterestList(BuyerInterest item)
	{
		if (searchAvailableItemList(item.getId()))
			return false;
		else
		{
			interestList_.put(item.getId(), item);
			return true;
		}
	}
	*/
	
	/* don't implement removeInterestList and searchInterestList for now
	public boolean removeInterestList(int itemId)
	{
		if (searchAvailableItemList(itemId))
		{
			// if item is found in the availableItemList_
			interestList_.remove(itemId);
			return true;
		}
		return false;
	}


	
	public boolean searchInterestList(int itemId)
	{
		return availableItemList_.containsKey(itemId); 
	}
	*/
	public synchronized void incrementInterestId()
	{
		interestId_++;
	}
	
	public BuyerInterest getInterest(int itemId)
	{
		// MUST call searchAvailableItemList to verify 
		// this item is in the availableItemList_ before call this function
		//return availableItemList_.get(itemId);
		return null;
	}
	

	public boolean addInterestList(Interest item)
	{
		interestList_.add(item);
		incrementInterestId();
		return true;
	}
	
	public void subscribeInterest (Interest item)
	{
		List<String> attributeList;
		String attribute="";
		String msg="";
		
		attributeList = item.getAttribute();
		ListIterator<String> it=attributeList.listIterator();
		while (it.hasNext())
		{
			attribute = attribute + it.next()+" ";
		}
		msg = "Buyer subscribe interest " + item.getName() + " " + String.valueOf(item.getMinBid()) + " " + attribute;
		sendBroker(msg);
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
	
	public void sendBroker (String sentMsg)
	{
		byte[] buf = new byte[256];
        buf = sentMsg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address_, port_);

        try
        {
        	mySocket_.send(packet);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}

	public void run()
	{
		processMessage(sendBrokerThenGetResponse("connect buyer"));
        while (openConnection) 
        {
        	
        }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
