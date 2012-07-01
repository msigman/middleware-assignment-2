import java.util.ArrayList;
import java.util.List;

public class AvailableItem implements AvailableItemInterface {
	protected int id_ = -1;
	protected String name_;
	protected List<String> attribute_ = new ArrayList<String>();
	protected Double minBid_ = -1.0;
	protected Double storedBid_ = -1.0;
	protected Double newBid_ = -2.0;
	
	public AvailableItem(int id, String name, double minBid)
	{
		id_ = id;
		name_= name;
		minBid_ = minBid;
	}
	
	public void addAttribute(String newAttribute)
	{
		attribute_.add(newAttribute);
	}
	
	public int getId() {
		return id_;
	}

	public String getName() {
		return name_;
	}

	public List<String> getAttribute() {
		return attribute_;
	}

	public double getMinBid() {
		return minBid_;
	}

	public double setMinBid(double newBid) {
		minBid_ = newBid;
		return minBid_;
	}

}
