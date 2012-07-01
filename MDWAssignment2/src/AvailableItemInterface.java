import java.util.List;

public interface AvailableItemInterface {
	
	public int getId();
	public String getName();
	public List<String> getAttribute();
	public double getMinBid();
	public double setMinBid(double newBid);
}
