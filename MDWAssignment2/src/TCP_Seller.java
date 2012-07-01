import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;


public class TCP_Seller extends Seller{
	private int port_;
	private ServerSocket mySocket_;
	private ServerSocket brokerSocket_;
	public static final String INVALID_COMMAND_ERROR   = "Invalid Command";
	public static final String INVALID_PARAMETER_ERROR = "Invalid Parameter";

	public TCP_Seller(int sellerId, int serverPort) {
		super(sellerId);
		port_ = serverPort;
		
		try {
			mySocket_ = new ServerSocket(port_);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		do
		{		  
		  //wait on a socket connection
		  Socket newConnection = null;
  		  try {
			newConnection = mySocket_.accept();			
		  } catch (IOException e) {
			e.printStackTrace();
			System.out.println("accept threw IO Exception");
			break;
          }
		  
	      // launch a thread to process the message
		  if (newConnection != null)
		  {
			  publishAvailableItem(newConnection);
		  }
		  else
		  {
			  System.out.println("accept returned NULL");
			  break;
		  }
		} while (true);

		try {
			mySocket_.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Closing server!");
	}

	public boolean subscribeAvailableItem(int itemId)
	{
		return true;
	}
	
	/**
	 * publish an available item and let the broker know that 
	 */
	public void publishAvailableItem(Socket socket, AvailableItem item)
	{
		if (mySocket_ != null)
		{
			BufferedReader input;
			BufferedWriter output;
			try {
				input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				//keep reading lines of commands from client
				String command = input.readLine();
				while (!command.isEmpty())
			    {
				  String response = manager_.processMessage(command);
				  output.write(response+"\n");
				  output.flush();
				  command = input.readLine();
				}

				mySocket_.close();

			} catch (IOException e) {
				// This will happen if the socket is closed while trying to get a new
				// command.  This is not an error if client is done
			}
		}
	}
	
	/**
	 * register a buyer to be notified when an available item is produced 
	 */
	public void subscribeBidUpdate(Buyer buy)
	{
		
	}
	
	/**
	 * publish an available item and let the broker know that 
	 */
	public void publishBidUpdate()
	{
		
	}
	
	/**
	 * register a buyer to be notified when an available item is produced 
	 */
	public void subscribeSaleFinalized(Buyer buy)
	{
		
	}
	
	/**
	 * publish an available item and let the broker know that 
	 */
	public void publishSaleFinalized()
	{
		
	}
	
	/**
	 * register a buyer to be notified when an available item is produced 
	 */
	public void getPublication()
	{
		
	}
	

	public String processMessage(String message)
	{
		StringTokenizer st = new StringTokenizer(message);
		
		//get command name
		String command = st.nextToken();
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
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
