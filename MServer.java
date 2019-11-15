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
		Message start=new Message("START",-1,id,"n/a","n/a");
		
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
	class ClientListener extends Thread
	{
		int id;
		public ClientListener(int id)
		{
			this.id=id;
		}
		public void run()
		{
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
					// TODO Auto-generated catch block
					e.printStackTrace();
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
