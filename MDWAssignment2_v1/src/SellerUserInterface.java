import java.io.IOException;
import java.util.StringTokenizer;


public class SellerUserInterface extends Thread {
	Seller seller_;
	protected boolean openUserInterface = true;
	
	public SellerUserInterface(Seller seller)
	{
		seller_ = seller;
	}
	
	public void run()
	{
		seller_.waitTilValidSellerId();
        while (openUserInterface) 
        {       	
        	getSellerUserRequest();
        }

	}
	
	protected String getSellerUserRequest()
	{
		String command = ""; 
		System.out.println("Enter 1 to add item ");
		System.out.println("Enter 2 to close active sale ");
		System.out.println("Enter 3 to quit ");
		try {
			command = seller_.input_.readLine();
			StringTokenizer st = new StringTokenizer(command);
			String temp = st.nextToken();
			
			if (temp.equalsIgnoreCase("1"))//"add item"))
			{
				if (st.countTokens() == 0)
				{
					return null;//seller_.handleSellerUserAddItem();
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
					seller_.openConnection = false;
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
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Seller sel1 = new Seller(4445);
		SellerUserInterface usr1 = new SellerUserInterface(sel1);
		sel1.start();
		usr1.start();

	}

}
