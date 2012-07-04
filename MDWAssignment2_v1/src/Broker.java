import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class Broker {
    protected DatagramSocket socket = null;
    protected BufferedReader input_ = null;
    protected boolean openConnection = true;
    protected int uniqueItemId_ = 0;
	protected ArrayList<AvailableItem> availableItemList_;
	protected HashMap<Integer,AvailableItem> bidUpdateItemList_;
	protected HashMap<Integer,AvailableItem> saleFinalizedItemList_;
	protected HashMap<Integer,BuyerInterest> _clientSubscribeInterestList;
	public static final String INVALID_COMMAND_ERROR   = "Invalid Command";
	public static final String INVALID_PARAMETER_ERROR = "Invalid Parameter";
	protected Client[] clients_;
	private int[] _childBrokers; // List of connected children brokers
	private int _parentBroker; // Parent broker (if -1, current broker node is root)


	public Broker(int brokerId) throws IOException {
		int port = 5000 + brokerId;
        socket = new DatagramSocket(port);
        input_ = new BufferedReader(new InputStreamReader(System.in));
		clients_ = new Client[2];
		clients_[0] = new Client();
		clients_[1] = new Client();
		
		switch(brokerId)
		{
		case 0: _childBrokers[0] = 1;
				_childBrokers[1] = 2;
				_childBrokers[2] = 3;
				_parentBroker = -1;
				break;
		case 1: _childBrokers[0] = 4;
				_childBrokers[1] = 5;
				_childBrokers[2] = 6;
				_parentBroker = 0;
				break;
		case 2: _childBrokers[0] = 7;
				_childBrokers[1] = 8;
				_childBrokers[2] = 9;
				_parentBroker = 0;
				break;			
		case 3: _childBrokers[0] = 10;
				_childBrokers[1] = 11;
				_childBrokers[2] = 12;
				_parentBroker = 0;
				break;
		case 4: _parentBroker = 1;
				break;
		case 5: _parentBroker = 1;
				break;
		case 6: _parentBroker = 1;
				break;
		case 7: _parentBroker = 2;
				break;
		case 8: _parentBroker = 2;
				break;
		case 9: _parentBroker = 2;
				break;
		case 10: _parentBroker = 3;
				break;
		case 11: _parentBroker = 3;
				break;
		case 12: _parentBroker = 3;
				break;
		
		}
	}
	
	public void run()
	{
		while (openConnection)
		{
			try
			{
	            byte[] buf = new byte[256];
	            String connectMsg;
	            
	                        
	            // receive request
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            
	    	    // check response
	            String received = new String(packet.getData(), 0, packet.getLength());
	            
	    		// send the response to the client at "address" and "port"
	            InetAddress address = packet.getAddress();
	            int port = packet.getPort();
	            connectMsg = processMessage(received, port, address);
	            buf = connectMsg.getBytes();
	            packet = new DatagramPacket(buf, buf.length, address, port);
	            socket.send(packet);
	            
	            /*
	            System.out.print("Press c if you wish to continue");
				String command = input_.readLine();
				StringTokenizer st = new StringTokenizer(command);
				String temp = st.nextToken();
				if (!temp.equalsIgnoreCase("c"))
				{
					openConnection = false;
				}
				*/
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	
		socket.close();
	}
	
	public int returnAvaiableClient()
	{
		for (int i=0; i<2; i++)
		{
			if (clients_[i].clientId_ == -1)
				return i;
		}
		return Constants.INVALID_CLIENT_ID;
	}
	
	public String processMessage(String message, int port, InetAddress address)
	{
		StringTokenizer st = new StringTokenizer(message);
		int clientId = Constants.INVALID_CLIENT_ID;
		
		//get command name
		String command = st.nextToken();
		if (command.equalsIgnoreCase("connect"))
		{
			String subCommand = st.nextToken();
			if (subCommand.equalsIgnoreCase("seller"))
			{
				// add to clients_ as a seller
				clientId = returnAvaiableClient();
				if (clientId != Constants.INVALID_CLIENT_ID)
				{
					clients_[clientId] = new Client(clientId, address, port, 0);
				}
			}
			else if (subCommand.equalsIgnoreCase("buyer"))
			{
				// add to clients_ as a buyer
				clientId = returnAvaiableClient();
				if (clientId != Constants.INVALID_CLIENT_ID)
				{
					clients_[clientId] = new Client(clientId, address, port, 1);
				}

			}
			command = "connected " + String.valueOf(clientId);
			System.out.println("broker recevies connect str from seller");
			return command;
		}
		else if (command.equalsIgnoreCase("Buyer"))
		{
			if (st.hasMoreTokens())
			{
				String subCommand1 = st.nextToken();
				if (subCommand1.equalsIgnoreCase("subscribe"))
				{
					if (st.hasMoreTokens())
					{
						String subCommand2 = st.nextToken();
						if (subCommand1.equalsIgnoreCase("interest"))
						{
							
						}
					}

				}
			}
			return command;
		}
		else if (command.equalsIgnoreCase("Seller"))
		{
			String subCommand = st.nextToken();
		}
		else if (command.equalsIgnoreCase("newItemId"))
		{
			uniqueItemId_++;
			command = String.valueOf(uniqueItemId_);
			System.out.println("broker recevies newItemId str from seller");
			return command;
		}
		else if (command.equalsIgnoreCase("Publish"))
		{
			String subCommand = st.nextToken();
			if (subCommand.equalsIgnoreCase("availableItem"))
			{
				// parse the AvailableItem structure
				AvailableItem item = new AvailableItem(message);
				availableItemList_.add(item);
				scanSubscriptionsForMatch(item);
				if(_parentBroker != -1) // Root item doesn't have a parent
				{
					sendNewAvailableItemToNode(5000 + _parentBroker, item);
				}
			}
			return subCommand;
		}
	    else
	    {
	    	return INVALID_COMMAND_ERROR;
	    }
		return command;
	}
	
	private void scanSubscriptionsForMatch(AvailableItem item)
	{
		// Check subscriptions list for matches
		for(int y = 0; y < _clientSubscribeInterestList.size(); y++)
		{
			if(doesInterestMatchItem(item, _clientSubscribeInterestList.get(y)))
			{
				// If found, send availableItem message to that node
				sendNewAvailableItemToNode(clients_[_clientSubscribeInterestList.get(y).buyerId_].port_, item);
			}
		}
	}
	
	// Returns true if there is a match between an interest and an available item
	private Boolean doesInterestMatchItem(AvailableItem item, BuyerInterest interest)
	{
		for(int y = 0; y < item.attribute_.size(); y++)
		{
			for(int x = 0; x < interest.attribute_.size(); x++)
			{
				if(item.attribute_.get(y) == item.attribute_.get(x))
						return true;
			}
		}
		return false;
	}
	
	// Sends command to parent broker with a description of the new item available
	private void sendNewAvailableItemToNode(int port, AvailableItem item)
	{
		String cmd = "Publish availableItem " + item.Serialize();
		InetAddress addy;
		try {
			addy = InetAddress.getLocalHost();
			DatagramPacket packet = new DatagramPacket(cmd.getBytes(), _parentBroker, addy, port);
	        socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int brokerId = Integer.parseInt(args[0]);
		Broker br1 = new Broker(brokerId);
		br1.run();
	}

}
