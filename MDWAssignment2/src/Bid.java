
public class Bid implements BidInterface{
	protected int id_ = -1;
	protected Double price_ = -1.0;
	
	public Bid(int id, double price)
	{
		id_ = id;
		price_ = price;
	}
	
	public int getId() {
		return id_;
	}

	public double getPrice() {
		return price_;
	}

	public double setPrice(double newPrice) {
		price_ = newPrice;
		return price_;
	}
	


}
