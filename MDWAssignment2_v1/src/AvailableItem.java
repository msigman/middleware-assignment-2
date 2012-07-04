import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AvailableItem {
	protected int itemId_ = -1;
	protected String name_;
	protected List<String> attribute_ = new ArrayList<String>();
	protected int minBid_ = -1;
	private int sellerId;
	
	public AvailableItem(int itemId, String name, int minBid, int sellerId)
	{
		itemId_ = itemId;
		name_= name;
		minBid_ = minBid;
		this.sellerId = sellerId;
	}
	
	// Returns a serialized version of the item
	public String Serialize()
	{
		String ret = this.sellerId + "|" + this.itemId_ + "|" + this.name_ + "|" + this.minBid_;
		for(int x=0;x<attribute_.size();x++)
		{
			ret = ret + this.attribute_.get(x) + "*";
		}
		return ret;
	}
	
	public AvailableItem(String msg)
	{
		String cmd="";
		String subCmd="";
		String itemId="";
		String name="";
		String minBid="";
		StringTokenizer st = new StringTokenizer(msg);
		
		cmd = st.nextToken();
		subCmd = st.nextToken();
		itemId = st.nextToken();
		name = st.nextToken();
		itemId = st.nextToken();
		minBid = st.nextToken();
		
		itemId_ = Integer.parseInt(itemId);
		name_= name;
		minBid_ = Integer.parseInt(minBid);
		
		while (st.hasMoreTokens())
		{
			this.addAttribute(st.nextToken());
		}
	}
	
	public void addAttribute(String newAttribute)
	{
		attribute_.add(newAttribute);
	}
	
	public int getId() {
		return itemId_;
	}
	
	public String getName() {
		return name_;
	}

	public List<String> getAttribute() {
		return attribute_;
	}

	public int getMinBid() {
		return minBid_;
	}

	public int setMinBid(int newBid) {
		minBid_ = newBid;
		return minBid_;
	}
}

class SellerAvailableItem extends AvailableItem {
	protected int sellerId_ = -1;
	protected int buyerId = -1; // buyerId that has the highest bid
	
	public SellerAvailableItem(int itemId, int sellerId, String name, int minBid)
	{
		super(itemId, name, minBid);
		sellerId_ = sellerId;
	}
}


class Bid
{
	int itemId_;
	int buyerId_;
	int price_;
	
	public Bid(int id, int price)
	{
		itemId_ = id;
		price_ = price;
	}
	
	public int getItemId() {
		return itemId_;
	}
	
	public int getBuyerId()
	{
		return buyerId_;
	}

	public int getPrice() {
		return price_;
	}

	public int setPrice(int newPrice) {
		price_ = newPrice;
		return price_;
	}
}

class BidUpdate
{
	int itemId_;
	int storedBid_;
	int newBid_;
	
	public BidUpdate(int itemId, int storedBid, int newBid) {
		itemId_ = itemId;
		storedBid_ = storedBid;
		newBid_ = newBid;
	}
}


class Interest
{
	String name_;
	int minBid_;
	List<String> attribute_;
	
	public Interest(String name, int minBid)
	{
		name_ = name;
		minBid_ = minBid;
	}
	
	public void addAttribute(String attribute)
	{
		attribute_.add(attribute);
	}
	
	public String getName() {
		return name_;
	}

	public List<String> getAttribute() {
		return attribute_;
	}

	public int getMinBid() {
		return minBid_;
	}
}

class BuyerInterest extends Interest
{
	int buyerId_;
	int interestId_;
	
	public BuyerInterest(String name, int minBid)
	{
		super(name, minBid);
	}
	
	public Object getId() {
		return interestId_;
	}
}

class Client
{
	int clientId_;
    InetAddress address_;
    int port_;
    int clientType_; // -1=not connected, 0=seller, 1=buyer
    
    public Client()
    {
		clientId_ = -1;
    	address_ = null;
    	port_ = -1;
    	clientType_ = -1; 
    }
    
    public Client(int clientId, InetAddress address, int port, int clientType)
    {
		clientId_ = clientId;
    	address_ = address;
    	port_ = port;
    	clientType_ = clientType; 
    }
    
    public InetAddress getAddress(Client cl)
    {
    	return cl.address_;
    }
    
    public int getPort(Client cl)
    {
    	return cl.port_;
    }
    
    public int getClientType(Client cl)
    {
    	return cl.clientType_;
    }
    
    public void setAddress(InetAddress address)
    {
    	address_ = address;
    }
    
    public void setPort(int port)
    {
    	port_ = port;
    }
    
    public void setClientType(int type)
    {
    	clientType_ = type;
    }
    
    public boolean comparePort(int port1, int port2)
    {
    	if (port1 == port2)
    		return true;
    	else
    		return false;
    }
}

class ConnectMsg
{
	String clientType_; // "seller" or "buyer"
	String connectMsg_; // this is the literal string "connect"
	
	public ConnectMsg(String connectMsg, String clientType)
	{
		clientType_ = clientType;
		connectMsg_ = connectMsg;
	}
	
	public String getClientType(ConnectMsg msg)
	{
		return msg.clientType_;
	}
	
	public String getConnectMsg(ConnectMsg msg)
	{
		return msg.connectMsg_;
	}

}
