package AOSp3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class MServer {
	
	protected final String[] clientIP= {"10.176.69.56","10.176.69.57"};
	protected final String[] serverIP= {"10.176.69.51","10.176.69.52","10.176.69.53","10.176.69.54","10.176.69.55"};
	protected final String MServerIP = "10.176.69.58";

	protected int numClients,numServers;
	final int clientStartingPort=5000;
	final int serverStartingPort=6000;
	final int reconnectionPort=7000;
	protected Socket[] clientSockets;
	protected Socket[] serverSockets;
	protected DataInputStream[] clientInStreams;
	protected DataOutputStream[] clientOutStreams;
	protected DataInputStream[] serverInStreams;
	protected DataOutputStream[] serverOutStreams;
	final int id=0;
	protected AtomicBoolean shutdown=new AtomicBoolean(false);
	
	public MServer(int numClients,int numServers)
	{
		this.numClients=numClients;
		this.numServers=numServers;
		clientSockets=new Socket[numClients];
		serverSockets=new Socket[numServers];
		clientInStreams=new DataInputStream[numClients];
		clientOutStreams=new DataOutputStream[numClients];
		serverInStreams=new DataInputStream[numServers];
		serverOutStreams=new DataOutputStream[numServers];
		
		for(int x=0;x<numClients;x++)
		{
			try 
			{
				ServerSocket ss= new ServerSocket(clientStartingPort+x);
				clientSockets[x]=ss.accept();
				clientInStreams[x]=new DataInputStream(clientSockets[x].getInputStream());
				clientOutStreams[x]=new DataOutputStream(clientSockets[x].getOutputStream());
				System.out.println("Client "+x+" connected");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All clients connected");
		for(int x=0;x<numServers;x++)
		{
			try 
			{
				ServerSocket ss= new ServerSocket(serverStartingPort+x);
				serverSockets[x]=ss.accept();
				serverInStreams[x]=new DataInputStream(serverSockets[x].getInputStream());
				serverOutStreams[x]=new DataOutputStream(serverSockets[x].getOutputStream());
				System.out.println("Server "+x+" connected");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("All servers connected.\nSetup complete, sending start signal to all clients");
		Message start=new Message("START",-1,id,"n/a");
		
		for(int x=0;x<numClients;x++)
		{
			(new ClientListener(id)).start();
			//public Message(String type,int filename,int senderId,String senderIp,String contents)
			
			try 
			{
				clientOutStreams[x].writeUTF(start.toString());
				clientOutStreams[x].flush();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int x=0;x<numServers;x++)
		{
			try 
			{
				serverOutStreams[x].flush();
				serverOutStreams[x].writeUTF(start.toString());
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	class ConnectionListener extends Thread
	{
		public void run()
		{
			while(true)
			{
				try 
				{
					ServerSocket ss=new ServerSocket(7000);
					Socket socket= ss.accept();
					String remoteIpAddress=socket.getInetAddress().toString().substring(1);
					
					if(strIndexOf(remoteIpAddress,clientIP)!=-1) //if the connection we got was from a client IP, add it to the client sockets
					{
						
						int connectingId=strIndexOf(remoteIpAddress,clientIP);
						System.out.println("Received connection from downed client with id="+connectingId);
						clientSockets[connectingId]=socket;
						clientInStreams[connectingId]=new DataInputStream(socket.getInputStream());
						clientOutStreams[connectingId]=new DataOutputStream(socket.getOutputStream());
						(new ClientListener(connectingId)).start();
					}
					else if(strIndexOf(remoteIpAddress,serverIP)!=-1)
					{
						int connectingId=strIndexOf(remoteIpAddress,serverIP);
						System.out.println("Received connection from downed server with id="+connectingId);
						serverSockets[connectingId]=socket;
						serverInStreams[connectingId]=new DataInputStream(socket.getInputStream());
						serverOutStreams[connectingId]=new DataOutputStream(socket.getOutputStream());
						(new ServerListener(connectingId)).start();
					}
					else
					{
						System.out.println("Connection received from bad IP address ("+remoteIpAddress+"). Discarding socket.");
						socket.close();
					}
				} 
				catch (IOException e) 
				{
					System.out.println("Error in receiving reconnection socket");
				}
				
			}
		}
	}
	private int strIndexOf(String string,String[] array)
	{
		for(int x=0;x<array.length;x++)
		{
			if(string.equals(array[x]))
				return x;
		}
		return -1;
	}
	class ClientListener extends Thread
	{
		int id;
		public ClientListener(int id)
		{
			this.id=id;
		}
		public void run()
		{
			System.out.println("Listener for client "+id+" started");
			while(true)
			{
				if(shutdown.get())	
					return;
				
				try 
				{
					Message msg=new Message(clientInStreams[id].readUTF());
				} 
				catch (IOException e) 
				{
					System.out.println("Client listener for server "+id+" crashed. Closing sockets");
					serverSockets[id]=null;
					serverInStreams[id]=null;
					serverOutStreams[id]=null;
					return;
				}
			}
		}
	}
	class ServerListener extends Thread
	{
		int id;
		public ServerListener(int id)
		{
			this.id=id;
		}
		public void run()
		{
			System.out.println("Listener for server "+id+" started");
			while(true)
			{
				if(shutdown.get())
				{
					return;	
				}
				
				try
				{
					String msg=serverInStreams[id].readUTF();
				}
				catch(IOException e)
				{
					System.out.println("Server listener for server "+id+" crashed. Closing sockets");
					try 
					{
						serverInStreams[id].close();
						serverOutStreams[id].close();
						serverSockets[id].close();
					} 
					catch (IOException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					serverSockets[id]=null;
					serverInStreams[id]=null;
					serverOutStreams[id]=null;
					return;
				}
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException 
	{
		File c=new File(args[0]);
		Scanner config=new Scanner(c);
		
		//int fileNum=Integer.parseInt(config.nextLine());
		int numClients=Integer.parseInt(config.nextLine());
		int numServers=Integer.parseInt(config.nextLine());
		//id clientcount #files
		config.close();
		new MServer(numClients,numServers);
	}
}
