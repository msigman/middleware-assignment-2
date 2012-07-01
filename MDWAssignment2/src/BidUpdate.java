
public class BidUpdate extends Bid{
	protected Double newBid_ = -2.0;
	protected int buyerId_ = -1;
	
	public BidUpdate(int id, double storedBid, double newBid, int buyerId) {
		super(id, storedBid);
		newBid_ = newBid;
		buyerId_ = buyerId;
	}
	
	public boolean updateBid(double newBid, int buyerId)
	{
		if (newBid <= price_)
			return false;
		else
		{
			buyerId_ = buyerId;
			return true;	
		}		
	}

}
