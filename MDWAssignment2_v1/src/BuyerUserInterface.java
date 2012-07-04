import java.io.IOException;
import java.util.StringTokenizer;


public class BuyerUserInterface extends Thread{
	Buyer buyer_;
	protected boolean openUserInterface = true;
	
	public BuyerUserInterface(Buyer buyer)
	{
		buyer_ = buyer;
	}
	
	public void run()
	{
		buyer_.waitTilValidBuyerId();
        while (openUserInterface) 
        {       	
        	getSellerUserRequest();
        }

	}
	
	protected String getSellerUserRequest()
	{
		String command = ""; 
		System.out.println("Enter 1 to publish interest ");
		System.out.println("Enter 2 to bid in user-driven mode ");
		System.out.println("Enter 3 to bid in automatic mode ");
		System.out.println("Enter 4 to show items under bid ");
		System.out.println("Enter 5 to show items match exactly interest ");
		System.out.println("Enter 6 to show items match one or more interest ");
		System.out.println("Enter 7 to quit ");

		try {
			command = buyer_.input_.readLine();
			StringTokenizer st = new StringTokenizer(command);
			String temp = st.nextToken();
			
			if (temp.equalsIgnoreCase("1"))//"add interest"))
			{
				if (st.countTokens() == 0)
				{
					return null;//buyer_.handleBuyerUserAddInterest();
				}
				else
					return Constants.INVALID_PARAMETER_ERROR;
			}
			else if (temp.equalsIgnoreCase("2"))//close active sale"))
			{
				if (st.countTokens() == 0)
				{
					return Constants.INVALID_PARAMETER_ERROR;
				}
				else
					return Constants.INVALID_PARAMETER_ERROR;
			}
			else if (temp.equalsIgnoreCase("3"))//quit"))
			{
				if (st.countTokens() == 0)
				{
					buyer_.openConnection = false;
					openUserInterface = false;
					return "quit";
				}
				else
					return Constants.INVALID_PARAMETER_ERROR;
			}
			else //(temp.equalsIgnoreCase("5"))//QUIT_COMMAND))
			{
				return Constants.INVALID_COMMAND_ERROR;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Constants.INVALID_COMMAND_ERROR;
	}

	public String handleBuyerUserAddInterest()
	{
		String name = "";
		String attribute = "";
		String minimumBid = "";
		
		System.out.println("Enter item's name: ");
		try {
			name = buyer_.input_.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Enter item’s attributes: ");
		try {
			attribute = buyer_.input_.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Enter item’s minimum bid: ");
		try {
			minimumBid = buyer_.input_.readLine();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Buyer subscribe interest "+name+" "+minimumBid+" "+attribute);
		Interest newItem = new Interest(name, Integer.parseInt(minimumBid));
		StringTokenizer st = new StringTokenizer(attribute);
		while (st.hasMoreTokens())
		{
			newItem.addAttribute(st.nextToken());
		}
		
		buyer_.addInterestList(newItem);
		//subscribeInterest(newItem);
		/*
		for (int index=0; index<st.countTokens(); index++)
		{
			newItem.addAttribute(st.nextToken());
		}
		*/
		return "addItem "+name+" "+minimumBid+" "+attribute;
	}
	
	public static void main(String[] args) {
		Buyer buy1 = new Buyer(4445);
		BuyerUserInterface usr1 = new BuyerUserInterface(buy1);
		buy1.start();
		usr1.start();

	}
}
