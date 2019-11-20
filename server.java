package AOSp3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;



public class server {

	protected final String[] clientIP= {"10.176.69.56","10.176.69.57"}; 
	protected final String[] serverIP= {"10.176.69.51","10.176.69.52","10.176.69.53","10.176.69.54","10.176.69.55"};
	protected final String MServerIP = "10.176.69.58";
	
	public Socket MServerSocket;
	public DataInputStream MServerInStream;
	public DataOutputStream MServerOutStream;
	
	public Socket[] clientSockets;
	public DataInputStream[] clientInStreams;
	public DataOutputStream[] clientOutStreams;
	
	final int MServerPort=6000;
	final int clientPort=6000;
	final int reconnectionPort=7000;
	
	protected int id;
	
	ArrayList<Chunk> chunks;
	
	public server(int id)
	{
		this.id=id;
		chunks=new ArrayList<Chunks>();
		connect(false);
	}
	
	public void connect(boolean reconnection)
	{
		try
		{
			if(reconnection)
			{
				MServerSocket=new Socket(MServerIP,reconnectionPort);
			}
			else
			{
				MServerSocket=new Socket(MServerIP,MServerPort+id);
			}
			MServerInStream=new DataInputStream(MServerSocket.getInputStream());
			System.out.println("Connected to Metadata Server");
		}
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			System.out.print("fail\n");
			e.printStackTrace();
			System.exit(0);
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		for(int x=0;x<clientIP.length;x++) //instantiates connections to clients
		{
			System.out.print("Connecting to client "+x+" ... ");
			try {
				if(reconnection)
				{
					clientSockets[x]=new Socket(MServerIP,reconnectionPort);
				}
				else
				{
					clientSockets[x]=new Socket(MServerIP,MServerPort+id);
				}
				
				System.out.print("success!\n");
				clientInStreams[x]=new DataInputStream(clientSockets[x].getInputStream());
				clientOutStreams[x]=new DataOutputStream(clientSockets[x].getOutputStream());
				
			} 
			catch (IOException e) 
			{
				System.out.print("failure.");
				System.exit(0);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int x=0;x<serverIP.length;x++) //instantiates connections to clients
		{
			if(x==id)
				continue;
			else
			{
				System.out.print("Connecting to server "+x+" ... ");
				try {
					if(reconnection)
					{
						clientSockets[x]=new Socket(MServerIP,reconnectionPort);
					}
					else
					{
						clientSockets[x]=new Socket(MServerIP,MServerPort+id);
					}
					
					System.out.print("success!\n");
					clientInStreams[x]=new DataInputStream(clientSockets[x].getInputStream());
					clientOutStreams[x]=new DataOutputStream(clientSockets[x].getOutputStream());
					
				} 
				catch (IOException e) 
				{
					System.out.print("failure.");
					System.exit(0);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void crash()
	{
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~\nSimulating server crash\n~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Server listener for server "+id+" crashed. Closing sockets");
		
		try
		{
			MServerInStream.close();
			MServerOutStream.close();
			MServerSocket.close();
			for(int x=0;x<clientSockets.length;x++)
			{
				clientInStreams[x].close();
				clientOutStreams[x].close();
				clientSockets[x].close();
				
			}
			clientSockets[id]=null;
			clientInStreams[id]=null;
			clientOutStreams[id]=null;
		}
		catch(IOException e)
		{
			System.out.println("Error in crashing");
			e.printStackTrace();
		}
		
		//*Wait here!*
		Random r= new Random();
		int wait=r.nextInt(1500);
		System.out.println("Waiting for "+(double)(wait/1000)+" seconds");
		try 
		{
			Thread.sleep(wait);
		} 
		catch (InterruptedException e) 
		{
			System.out.println("Error while waiting");
		}
		connect(true);
		return;
	}
	class ClientListener implements Thread
	{
		int threadId;
		public ClientListener(int id)
		{
			threadId=id;
		}
		public void run()
		{
			while(true)
			{
				Message msg=new Message(clientInStreams[threadId].readUTF());
				if(msg.getType().equals("READY")) 
				{
					Message reply=new Message("READYACK",msg.getFilename(),threadId,"ready");
					clientOutStreams[threadId].writeUTF(reply.toString);
					clientOutStreams[threadId].flush();
				}
				else if(msg.getType().equals("COMMIT"))
				{
					//TODO: Add code here to append to a chunk
					
				}
				else if(msg.getType().equals("READ"))
				{
					int offset=Integer.parseInt(msg.getContents());
					
				}
				else
				{
					System.out.println("Received malformed message: "+msg.toString());
				}
			}
		}
	}
	class ServerListener extends Thread
	{
		int threadId;
		public ServerListener(int id)
		{
			threadId=id;
		}
		public void run()
		{
			
		}
	}
	class ConnectionListener extennds Thread
	{
		public ConnectionListener()
		{
			
		}
		public void run()
		{
			
		}
	}
	public static void main(String[] args) throws FileNotFoundException
	{
		File c=new File(args[0]);
		Scanner config=new Scanner(c);
		
		int id=Integer.parseInt(config.nextLine());
		//int fileNum=Integer.parseInt(config.nextLine());
		//id clientcount #files
		config.close();
		new server(id);
	}
}
